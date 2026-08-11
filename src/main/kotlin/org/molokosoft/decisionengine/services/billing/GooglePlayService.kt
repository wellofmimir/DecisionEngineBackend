package org.molokosoft.decisionengine.services.billing

import com.google.auth.oauth2.GoogleCredentials
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.FileInputStream

class GooglePlayService(
    private val httpClient: OkHttpClient
) {

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

    fun getSubscription(
        packageName: String,
        purchaseToken: String
    ): String {

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

                return body
                    ?: error(
                        "Google Play API returned an empty response."
                    )
            }
    }
}