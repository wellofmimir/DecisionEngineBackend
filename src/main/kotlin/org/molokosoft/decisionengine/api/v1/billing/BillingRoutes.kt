package org.molokosoft.decisionengine.api.v1.billing

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.json.Json
import org.molokosoft.decisionengine.api.v1.billing.model.dto.RealTimeDeveloperNotification
import org.molokosoft.decisionengine.api.v1.billing.model.requests.PubSubPushRequest

import org.molokosoft.decisionengine.api.v1.billing.model.requests.VerifyPurchaseRequest
import org.molokosoft.decisionengine.api.v1.billing.model.responses.VerifyPurchaseResponse
import org.molokosoft.decisionengine.api.v1.model.ApiResponse
import org.molokosoft.decisionengine.authentication.ApiKeyGenerator
import org.molokosoft.decisionengine.authentication.ApiKeyHasher
import org.molokosoft.decisionengine.authentication.AuthenticationNames
import org.molokosoft.decisionengine.extensions.receiveValidated
import org.molokosoft.decisionengine.repositories.users.UserRepository
import org.molokosoft.decisionengine.services.billing.GooglePlayService
import java.util.Base64

import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

const val PACKAGE_NAME_DECISIONENGINE = "com.molokosoft.decisionengine"

@OptIn(ExperimentalTime::class)
fun Route.billingRoutes(
    userRepository: UserRepository,
    googlePlayService: GooglePlayService
) {
    route("/billing") {
        rateLimit(RateLimitName("billing")) {
            post("/verify") {
                val request = call.receiveValidated<VerifyPurchaseRequest>()

                val subscription =
                    googlePlayService.getSubscription(
                        packageName = PACKAGE_NAME_DECISIONENGINE,
                        purchaseToken = request.purchaseToken
                    )

                println("Google Play subscription:")
                println(subscription)

                val apiKey = ApiKeyGenerator.generate()
                val apiKeyHash = ApiKeyHasher.sha256(apiKey)

                val expiresAt = (Clock.System.now() + 3.days).toEpochMilliseconds()
                userRepository.insertApiKey(apiKeyHash, request.purchaseToken, 100, expiresAt)
                userRepository.activateApiKey(apiKeyHash)

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

        authenticate(AuthenticationNames.RTDN) {
            post("/rtdn") {
                val request = call.receive<PubSubPushRequest>()
                println("Received Pub/Sub message: ${request.message.messageId}")

                val decodedData =
                    Base64
                        .getDecoder()
                        .decode(request.message.data)
                        .toString(Charsets.UTF_8)

                println("Decoded data: $decodedData")

                val notification =
                    Json.decodeFromString<RealTimeDeveloperNotification>(
                        decodedData
                    )

                if (notification.packageName != PACKAGE_NAME_DECISIONENGINE) {
                    println("Ignoring RTDN for package: ${notification.packageName}")
                    call.respond(HttpStatusCode.OK)
                    return@post
                }

                if (notification.testNotification != null) {
                    println("Received Google Play RTDN test notification")

                    call.respond(HttpStatusCode.OK)
                    return@post
                }

                val subscriptionNotification =
                    notification.subscriptionNotification

                if (subscriptionNotification == null) {
                    println("RTDN does not contain a subscription notification.")
                    call.respond(HttpStatusCode.OK)
                    return@post
                }

                val purchaseToken =
                    subscriptionNotification
                        .purchaseToken

                println("Subscription notification type: ${subscriptionNotification.notificationType}")
                println("Purchase token: $purchaseToken")

                val subscription =
                    googlePlayService.getSubscription(
                        packageName = PACKAGE_NAME_DECISIONENGINE,
                        purchaseToken = purchaseToken
                    )

                println("Google Play subscription: ")
                println(subscription)

                call.respond(HttpStatusCode.OK)
            }
        }
    }
}