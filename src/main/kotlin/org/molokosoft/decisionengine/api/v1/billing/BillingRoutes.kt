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
import io.ktor.server.routing.get
import kotlinx.serialization.json.Json
import org.molokosoft.decisionengine.api.v1.billing.model.Product
import org.molokosoft.decisionengine.api.v1.billing.model.ProductType
import org.molokosoft.decisionengine.api.v1.billing.model.dto.AccessStatus
import org.molokosoft.decisionengine.api.v1.billing.model.dto.RealTimeDeveloperNotification
import org.molokosoft.decisionengine.api.v1.billing.model.requests.PubSubPushRequest

import org.molokosoft.decisionengine.api.v1.billing.model.requests.VerifyPurchaseRequest
import org.molokosoft.decisionengine.api.v1.billing.model.responses.AccessStatusResponse
import org.molokosoft.decisionengine.api.v1.billing.model.responses.VerifyPurchaseResponse
import org.molokosoft.decisionengine.api.v1.model.ApiError
import org.molokosoft.decisionengine.api.v1.model.ApiResponse
import org.molokosoft.decisionengine.authentication.ApiKeyGenerator
import org.molokosoft.decisionengine.authentication.ApiKeyHasher
import org.molokosoft.decisionengine.authentication.AuthenticationNames
import org.molokosoft.decisionengine.extensions.receiveValidated
import org.molokosoft.decisionengine.repositories.users.UserRepository
import org.molokosoft.decisionengine.services.billing.GooglePlayService
import java.util.Base64

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

const val PACKAGE_NAME_DECISIONENGINE = "com.molokosoft.decisionengine"

@OptIn(ExperimentalTime::class)
fun Route.billingRoutes(
    userRepository: UserRepository,
    googlePlayService: GooglePlayService
) {
    route("/billing") {
        rateLimit(RateLimitName("billing")) {
            get("/status") {
                val apiKey =
                    call.request
                        .headers["Authorization"]
                        ?.removePrefix("Bearer ")
                        ?.trim()

                if (apiKey.isNullOrBlank()) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse(
                            success = false,
                            data = ApiError(
                                code = HttpStatusCode.Unauthorized.toString(),
                                message = "Unauthorized."
                            )
                        )
                    )

                    return@get
                }

                val apiKeyHash =
                    ApiKeyHasher.sha256(apiKey)

                val existingApiKey =
                    userRepository.findApiKeyHash(apiKeyHash)

                if (existingApiKey == null || !existingApiKey.isActive) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse(
                            success = false,
                            data = ApiError(
                                code = HttpStatusCode.Unauthorized.toString(),
                                message = "Invalid API key."
                            )
                        )
                    )

                    return@get
                }

                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        success = true,
                        data = AccessStatusResponse(
                            accessStatus = AccessStatus(
                                existingApiKey.remainingUsages
                            )
                        )
                    )
                )
            }

            post("/verify") {
                val request = call.receiveValidated<VerifyPurchaseRequest>()

                val product =
                    Product.entries.firstOrNull() {
                        it.productId == request.productId
                    } ?: run {

                        call.respond(
                            HttpStatusCode.Forbidden,
                            ApiResponse(
                                success = false,
                                data = ApiError(
                                    code = HttpStatusCode.Forbidden.toString(),
                                    message = "Unknown product."
                                )
                            )
                        )

                        return@post
                    }

                when (product.type) {
                    ProductType.CONSUMABLE -> {
                        val purchase =
                            googlePlayService.getProductPurchase(
                                packageName = PACKAGE_NAME_DECISIONENGINE,
                                purchaseToken = request.purchaseToken,
                                productId = product.productId
                            )

                        if (purchase.purchaseState != 0) {
                            call.respond(
                                HttpStatusCode.Forbidden,
                                ApiResponse(
                                    success = false,
                                    data = ApiError(
                                        code = HttpStatusCode.Forbidden.toString(),
                                        message = "Purchase is not completed."
                                    )
                                )
                            )

                            return@post
                        }

                        if (purchase.consumptionState != 0) {
                            call.respond(
                                HttpStatusCode.Forbidden,
                                ApiResponse(
                                    success = false,
                                    data = ApiError(
                                        code = HttpStatusCode.Forbidden.toString(),
                                        message = "Purchase has already been consumed."
                                    )
                                )
                            )

                            return@post
                        }

                        if (userRepository.purchaseExists(request.purchaseToken)) {
                            call.respond(
                                HttpStatusCode.Forbidden,
                                ApiResponse(
                                    success = false,
                                    data = ApiError(
                                        code = HttpStatusCode.Forbidden.toString(),
                                        message = "Purchase has already been processed."
                                    )
                                )
                            )

                            return@post
                        }

                        if (request.apiKey.isNullOrBlank()) {

                            val consumeSuccessful =
                                googlePlayService.consumeProduct(
                                    packageName = PACKAGE_NAME_DECISIONENGINE,
                                    productId = product.productId,
                                    purchaseToken = request.purchaseToken
                                )

                            if (!consumeSuccessful) {
                                call.respond(
                                    status = HttpStatusCode.InternalServerError,
                                    message = ApiResponse(
                                        success = false,
                                        data = ApiError(
                                            code = HttpStatusCode.InternalServerError.toString(),
                                            message = "Purchase failed."
                                        )
                                    )
                                )

                                return@post
                            }

                            val apiKey =
                                ApiKeyGenerator.generate()

                            val apiKeyHash =
                                ApiKeyHasher.sha256(apiKey)

                            userRepository.insertApiKey(apiKeyHash, request.purchaseToken, product.usageLimit, null)
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

                            return@post

                        } else {

                            val apiKeyHash =
                                ApiKeyHasher.sha256(request.apiKey)

                            val existingApiKey =
                                userRepository.findApiKeyHash(apiKeyHash)

                            if (existingApiKey == null) {
                                call.respond(
                                    HttpStatusCode.Forbidden,
                                    ApiResponse(
                                        success = false,
                                        data = ApiError(
                                            code = HttpStatusCode.Forbidden.toString(),
                                            message = "Invalid API key."
                                        )
                                    )
                                )

                                return@post
                            }

                            userRepository.addUsages(
                                usages = product.usageLimit,
                                apiKeyHash = existingApiKey.apiKeyHash
                            )

                            val consumeSuccessful =
                                googlePlayService.consumeProduct(
                                    packageName = PACKAGE_NAME_DECISIONENGINE,
                                    productId = product.productId,
                                    purchaseToken = request.purchaseToken
                                )

                            if (!consumeSuccessful) {
                                call.respond(
                                    status = HttpStatusCode.InternalServerError,
                                    message = ApiResponse(
                                        success = false,
                                        data = ApiError(
                                            code = HttpStatusCode.InternalServerError.toString(),
                                            message = "Purchase failed."
                                        )
                                    )
                                )

                                return@post
                            }

                            userRepository.insertPurchaseToken(
                                request.purchaseToken,
                                existingApiKey.id
                            )

                            call.respond(
                                status = HttpStatusCode.OK,
                                message = ApiResponse(
                                    success = true,
                                    data = VerifyPurchaseResponse(
                                        apiKey = request.apiKey
                                    )
                                )
                            )

                            return@post
                        }
                    }

                    ProductType.SUBSCRIPTION -> {
                        val subscription =
                            googlePlayService.getSubscription(
                                packageName = PACKAGE_NAME_DECISIONENGINE,
                                purchaseToken = request.purchaseToken
                            )

                        if (subscription.subscriptionState != "SUBSCRIPTION_STATE_ACTIVE") {
                            call.respond(
                                status = HttpStatusCode.Forbidden,
                                message = ApiResponse(
                                    success = false,
                                    data = ApiError(
                                        code = HttpStatusCode.Forbidden.toString(),
                                        message = "Forbidden."
                                    )
                                )
                            )

                            return@post
                        }

                        val lineItem =
                            subscription.lineItems.firstOrNull()
                                ?: run {
                                    call.respond(
                                        status = HttpStatusCode.Forbidden,
                                        message = ApiResponse(
                                            success = false,
                                            data = ApiError(
                                                code = HttpStatusCode.Forbidden.toString(),
                                                message = "Forbidden."
                                            )
                                        )
                                    )

                                    return@post
                                }

                        println("Google Play subscription:")
                        println(subscription)

                        val expiresAt =
                            Instant.parse(lineItem.expiryTime).toEpochMilliseconds()

                        val apiKey =
                            ApiKeyGenerator.generate()

                        val apiKeyHash =
                            ApiKeyHasher.sha256(apiKey)

                        userRepository.insertApiKey(apiKeyHash, request.purchaseToken, product.usageLimit, expiresAt)
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

                        return@post
                    }
                }
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

                val json =
                    Json {
                        ignoreUnknownKeys = true
                    }

                val notification =
                    json.decodeFromString<RealTimeDeveloperNotification>(
                        decodedData
                    )

                if (notification.packageName != PACKAGE_NAME_DECISIONENGINE) {
                    println("Ignoring RTDN for package: ${notification.packageName}")
                    call.respond(HttpStatusCode.OK)
                    return@post
                }

                if (notification.testNotification != null) {
                    println("Received Google Play RTDN test notification.")

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