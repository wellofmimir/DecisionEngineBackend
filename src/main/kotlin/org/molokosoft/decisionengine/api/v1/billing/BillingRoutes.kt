package org.molokosoft.decisionengine.api.v1.billing

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

import org.molokosoft.decisionengine.api.v1.billing.model.requests.VerifyPurchaseRequest
import org.molokosoft.decisionengine.api.v1.billing.model.responses.VerifyPurchaseResponse
import org.molokosoft.decisionengine.api.v1.model.ApiResponse
import org.molokosoft.decisionengine.authentication.ApiKeyGenerator
import org.molokosoft.decisionengine.authentication.ApiKeyHasher
import org.molokosoft.decisionengine.authentication.AuthenticationNames
import org.molokosoft.decisionengine.extensions.receiveValidated
import org.molokosoft.decisionengine.repositories.users.UserRepository

import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Route.billingRoutes(
    userRepository: UserRepository
) {
    route("/billing") {
        authenticate(AuthenticationNames.API_KEY) {

        }

        rateLimit(RateLimitName("billing")) {
            post("/verify") {
                val request = call.receiveValidated<VerifyPurchaseRequest>()

                //mit PurchaseToken google developer api fragen, ob kauf stattgefunden hat und gut ist
                //dann entscheiden, wie lange der API Key gilt -> trial, oder 7-Tage oder 1 Jahr

                val apiKey = ApiKeyGenerator.generate()
                val apiKeyHash = ApiKeyHasher.sha256(apiKey)

                val expiresAt = (Clock.System.now() + 3.days).toEpochMilliseconds()
                userRepository.insertApiKey(apiKeyHash, request.purchaseToken, 100, expiresAt)

                call.respond(
                    status = HttpStatusCode.OK,
                    message = ApiResponse(
                        success = true,
                        data = VerifyPurchaseResponse(
                            apiKey = apiKey
                        )
                    )
                )
            }
        }
    }
}