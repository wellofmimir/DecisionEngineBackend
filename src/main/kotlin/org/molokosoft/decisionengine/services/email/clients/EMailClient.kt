package org.molokosoft.decisionengine.services.email.clients

import okhttp3.OkHttpClient
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


import org.molokosoft.decisionengine.services.email.SendEMailRequest
import java.lang.RuntimeException

class EMailClient(
    private val client: OkHttpClient
) {
    fun sendEMail(sendEMailRequest: SendEMailRequest): Boolean {
        val body = Json.encodeToString(sendEMailRequest)
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://api.brevo.com/v3/smtp/email")
            .addHeader("Content-Type", "application/json")
            .addHeader("api-key", "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")
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()

                if (!response.isSuccessful)
                    throw RuntimeException("Brevo Error ${response.code}: ${response.body?.string()}")
            }

            return true
        } catch (e: Exception) {
            return false
        }
    }
}