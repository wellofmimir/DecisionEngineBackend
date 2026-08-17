package org.molokosoft.decisionengine

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import io.ktor.server.routing.route
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.bearer
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import org.molokosoft.decisionengine.api.v1.articles.articleRoutes
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import io.ktor.server.auth.jwt.JWTPrincipal
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json

import org.slf4j.event.Level

import org.molokosoft.decisionengine.api.v1.decision.decisionRoutes
import org.molokosoft.decisionengine.api.v1.email.emailRoutes
import org.molokosoft.decisionengine.api.v1.health.getHealth
import org.molokosoft.decisionengine.api.v1.model.ApiError
import org.molokosoft.decisionengine.api.v1.model.ApiResponse
import org.molokosoft.decisionengine.api.v1.criteria.criteriaRoutes
import org.molokosoft.decisionengine.api.v1.quote.quoteRoutes
import org.molokosoft.decisionengine.database.DatabaseFactory
import org.molokosoft.decisionengine.exceptions.BadRequestException
import org.molokosoft.decisionengine.jobs.EndOfTrialMailJob
import org.molokosoft.decisionengine.scheduler.Scheduler
import org.molokosoft.decisionengine.services.Services
import org.molokosoft.decisionengine.api.v1.billing.billingRoutes
import org.molokosoft.decisionengine.api.v1.feedback.feedbackRoutes
import org.molokosoft.decisionengine.api.v1.security.securityRoutes
import org.molokosoft.decisionengine.authentication.ApiKeyHasher
import org.molokosoft.decisionengine.authentication.AuthenticationNames
import org.molokosoft.decisionengine.authentication.principals.ApiKeyPrincipal
import org.molokosoft.decisionengine.jobs.DailyArticleJob
import org.molokosoft.decisionengine.extensions.*

import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


fun main() {
    embeddedServer(Netty, 45003) {

        DatabaseFactory.init()

        val services = Services()
        val scheduler = Scheduler()

        System.getenv("GOOGLE_APPLICATION_CREDENTIALS")
            ?: error("GOOGLE_APPLICATION_CREDENTIALS environment variable is not set")

        val rtdnAudience = System.getenv("RTDN_AUDIENCE")
            ?: error("RTDN_AUDIENCE environment variable is not set")

        val rtdnServiceAccountEmail = System.getenv("RTDN_SERVICE_ACCOUNT_EMAIL")
            ?: error("RTDN_SERVICE_ACCOUNT_EMAIL environment variable is not set")

        val jwkProvider: JwkProvider =
            JwkProviderBuilder(
                URL("https://www.googleapis.com/oauth2/v3/certs")
            )
                .cached(10, 24, TimeUnit.HOURS)
                .rateLimited(10, 1, TimeUnit.MINUTES)
                .build()

        install(Authentication) {
            bearer(AuthenticationNames.API_KEY) {
                realm = "DecisionEngine API"

                authenticate { tokenCredential ->

                    val apiKey = tokenCredential.token
                    val apiKeyHash = ApiKeyHasher.sha256(apiKey)
                    val apiKeyEntry = services.userRepository.findApiKeyHash(apiKeyHash) ?:
                        return@authenticate null

                    if (!apiKeyEntry.isActive)
                        return@authenticate null

                    ApiKeyPrincipal(apiKeyEntry.id)
                }
            }

            jwt(AuthenticationNames.RTDN) {
                realm = "DecisionEngine RTDN"

                verifier(
                    jwkProvider,
                    issuer = "https://accounts.google.com"
                )

                validate { credential ->

                    val email =
                        credential.payload
                            .getClaim("email")
                            .asString()

                    val audience =
                        credential.payload
                            .audience

                    if (email != rtdnServiceAccountEmail) {
                        println("RTDN rejected: invalid email")
                        return@validate null
                    }

                    if (!audience.contains(rtdnAudience)) {
                        println("RTDN rejected: invalid audience")
                        return@validate null
                    }

                    JWTPrincipal(credential.payload)
                }
            }
        }

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }

        install(RateLimit) {
            register(RateLimitName("promptReconnaissance")) {
                val limit = 20

                rateLimiter(
                    limit = limit,
                    refillPeriod = 60.seconds
                )

                modifyResponse { call, state ->
                    call.response.headers.append(
                        "X-RateLimit-Limit",
                        "$limit"
                    )
                }
            }

            register(RateLimitName("quotes")) {
                val limit = 10

                rateLimiter(
                    limit = limit,
                    refillPeriod = 24.hours
                )

                modifyResponse { call, state ->
                    call.response.headers.append(
                        "X-RateLimit-Limit",
                        "$limit"
                    )
                }
            }

            register(RateLimitName("feedback")) {
                val limit = 1

                rateLimiter(
                    limit = limit,
                    refillPeriod = 12.hours
                )
            }

            register(RateLimitName("articles")) {
                val limit = 10

                rateLimiter(
                    limit = limit,
                    refillPeriod = 24.hours
                )

                modifyResponse { call, state ->
                    call.response.headers.append(
                        "X-RateLimit-Limit",
                        "$limit"
                    )
                }
            }

            register(RateLimitName("email")) {
                val limit = 10

                rateLimiter(
                    limit = limit,
                    refillPeriod = 60.seconds
                )

                modifyResponse { call, state ->
                    call.response.headers.append(
                        "X-RateLimit-Limit",
                        "$limit"
                    )
                }
            }

            register(RateLimitName("health")) {
                val limit = 100

                rateLimiter(
                    limit = limit,
                    refillPeriod = 60.seconds
                )

                modifyResponse { call, state ->
                    call.response.headers.append(
                        "X-RateLimit-Limit",
                        "$limit"
                    )
                }
            }

            register(RateLimitName("decision")) {
                val limit = 3

                rateLimiter(
                    limit = limit,
                    refillPeriod = 30.seconds
                )

                modifyResponse { call, state ->
                    call.response.headers.append(
                        "X-RateLimit-Limit",
                        "$limit"
                    )
                }
            }

            register(RateLimitName("safetyClassification")) {
                val limit = 20

                requestKey { call ->
                    call.requireInstallationId()
                }

                rateLimiter(
                    limit = limit,
                    refillPeriod = 60.seconds
                )

                modifyResponse { call, state ->
                    call.response.headers.append(
                        "X-RateLimit-Limit",
                        "$limit"
                    )
                }
            }

            register(RateLimitName("criteria")) {
                val limit = 3

                requestKey { call ->
                    call.requireInstallationId()
                }

                rateLimiter(
                    limit = limit,
                    refillPeriod = 30.seconds
                )

                modifyResponse { call, state ->
                    call.response.headers.append(
                        "X-RateLimit-Limit",
                        "$limit"
                    )
                }
            }

            register(RateLimitName("billing")) {
                val limit = 10

                rateLimiter(
                    limit = limit,
                    refillPeriod = 1.minutes
                )

                modifyResponse { call, state ->
                    call.response.headers.append(
                        "X-RateLimit-Limit",
                        "$limit"
                    )
                }
            }
        }

        install(CallLogging) {
            level = Level.INFO

            format { call ->
                val status = call.response.status()?.value ?: 0

                "${call.request.httpMethod.value} " +
                "${call.request.uri} " +
                "status=$status"
            }
        }

        install(StatusPages) {
            status(HttpStatusCode.TooManyRequests) { call, status ->
                call.respond(
                    status = status,
                    message = ApiResponse(
                        success = false,
                        data = null,
                        error = ApiError(
                            code = status.toString(),
                            message = "Too many requests."
                        )
                    )
                )
            }

            status(HttpStatusCode.InternalServerError) { call, status ->
                call.respond(
                    status = status,
                    message = ApiResponse(
                        success = false,
                        data = null,
                        error = ApiError(
                            code = status.toString(),
                            message = "Internal server error."
                        )
                    )
                )
            }

            exception<JsonConvertException> { call, cause ->
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = ApiResponse(
                        success = false,
                        data = null,
                        error = ApiError(
                            code = "INVALID_REQUEST",
                            message = "Invalid request."
                        )
                    )
                )
            }

            exception<BadRequestException> { call, cause ->
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = ApiResponse(
                        success = false,
                        data = null,
                        error = ApiError(
                            code = "INVALID_REQUEST",
                            message = cause.message
                        )
                    )
                )
            }
        }

        scheduler.every(
            24.hours,
            EndOfTrialMailJob(services.userRepository, services.eMailService)
        )

        scheduler.every(
            24.hours,
            DailyArticleJob(services.articlesRepository, services.articlesService)
        )

        routing {
            getHealth()

            route("/api/v1/") {
                billingRoutes(
                    services.userRepository,
                    services.googlePlayService
                )

                emailRoutes(services.eMailService)
                decisionRoutes(services.decisionService)
                articleRoutes(services.articlesRepository)
                feedbackRoutes(services.feedbackFileService)
                quoteRoutes(services.quoteFileService)
                criteriaRoutes(services.criteriaService)
                securityRoutes(services.securityService)
            }
        }

    }.start(wait = true)
}