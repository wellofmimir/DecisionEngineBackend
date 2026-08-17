package org.molokosoft.decisionengine.services.billing

import com.google.auth.oauth2.GoogleCredentials
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.molokosoft.decisionengine.api.v1.billing.model.dto.ProductPurchase
import org.molokosoft.decisionengine.api.v1.billing.model.dto.SubscriptionPurchaseV2
import java.io.FileInputStream

class GooglePlayService(
    private val httpClient: OkHttpClient
) {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val credentials =
        GoogleCredentials
            .fromStream(
                FileInputStream(
                    System.getenv("GOOGLE_APPLICATION_CREDENTIALS")
                )
            )
            .createScoped(
                listOf(
                    "https://www.googleapis.com/auth/androidpublisher"
                )
            )

    fun getAccessToken(): String {
        credentials.refreshIfExpired()

        return credentials
            .accessToken
            .tokenValue
    }

    fun consumeProduct(
        packageName: String,
        productId: String,
        purchaseToken: String
    ): Boolean {
        val accessToken =
            getAccessToken()

        val url =
            "https://androidpublisher.googleapis.com" +
                    "/androidpublisher/v3/applications/" +
                    "$packageName/purchases/products/" +
                    "$productId/tokens/" +
                    "$purchaseToken:consume"

        val request =
            Request.Builder()
                .url(url)
                .post(
                    "".toRequestBody(
                        "application/json; charset=utf-8".toMediaType()
                    )
                )
                .addHeader(
                    "Authorization",
                    "Bearer $accessToken"
                )
                .addHeader(
                    "Accept",
                    "application/json"
                )
                .build()

        httpClient
            .newCall(request)
            .execute()
            .use { response ->

                val body =
                    response.body?.string()

                return response.isSuccessful
            }
    }

    fun getProductPurchase(
        packageName: String,
        productId: String,
        purchaseToken: String
    ): ProductPurchase {

        val accessToken =
            getAccessToken()

        val url =
            "https://androidpublisher.googleapis.com" +
                    "/androidpublisher/v3/applications/" +
                    "$packageName/purchases/products/" +
                    "$productId/tokens/" +
                    purchaseToken

        val request =
            Request.Builder()
                .url(url)
                .get()
                .addHeader(
                    "Authorization",
                    "Bearer $accessToken"
                )
                .addHeader(
                    "Accept",
                    "application/json"
                )
                .build()

        httpClient
            .newCall(request)
            .execute()
            .use { response ->
                val body =
                    response.body?.string()

                if (!response.isSuccessful) {
                    throw RuntimeException(
                        "Google Play API failed: ${response.code} $body"
                    )
                }

                return json.decodeFromString<ProductPurchase>(body
                    ?: error(
                        "Google Play API returned an empty response."
                    )
                )
            }
    }

    fun getSubscription(
        packageName: String,
        purchaseToken: String
    ): SubscriptionPurchaseV2 {

        val accessToken =
            getAccessToken()

        val url =
            "https://androidpublisher.googleapis.com" +
             "/androidpublisher/v3/applications/" +
             "$packageName/purchases/subscriptionsv2/tokens/" +
             purchaseToken

        val request =
            Request
                .Builder()
                .url(url)
                .get()
                .addHeader(
                    "Authorization",
                    "Bearer $accessToken"
                )
                .addHeader(
                    "Accept",
                    "application/json"
                )
                .build()

        httpClient
            .newCall(request)
            .execute()
            .use { response ->
                val body =
                    response.body?.string()

                if (!response.isSuccessful) {
                    throw RuntimeException(
                        "Google Play API failed: ${response.code} $body"
                    )
                }

                return json.decodeFromString<SubscriptionPurchaseV2>(body
                    ?: error(
                            "Google Play API returned an empty response."
                        )
                )
            }
    }
}