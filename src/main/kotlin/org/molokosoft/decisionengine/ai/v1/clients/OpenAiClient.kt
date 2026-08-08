package org.molokosoft.decisionengine.ai.v1.clients

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.molokosoft.decisionengine.api.v1.articles.model.dto.DailyArticle
import org.molokosoft.decisionengine.api.v1.criteria.model.dto.CriterionSuggestion
import org.molokosoft.decisionengine.api.v1.decision.model.dto.DecisionAnalysisResult
import org.molokosoft.decisionengine.api.v1.decision.model.dto.SafetyClassification

class OpenAiClient(
    private val client: OkHttpClient
) : AiClient {

    override suspend fun analyze(systemPrompt: String, prompt: String): DecisionAnalysisResult? {

        val mediaType = "application/json; charset=utf-8".toMediaType()

        val systemPrompt = JSONObject().apply {
            put("role", "system")
            put("content", systemPrompt)
        }

        val userPrompt = JSONObject().apply {
            put("role", "user")
            put("content", prompt)
        }

        val promptsArray = JSONArray().apply {
            put(systemPrompt)
            put(userPrompt)
        }

        val jsonRequestBody = JSONObject().apply {
            put("model", "gpt-4.1-mini")
            put("messages", promptsArray)
            put("temperature", 0.2)

        }.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", System.getenv("OPENAI_KEY"))
            .post(jsonRequestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: ""
                val responseJson = JSONObject(responseBody)
                val choicesArray = responseJson.getJSONArray("choices")
                val choice = choicesArray.getJSONObject(0)
                val message = choice.getJSONObject("message")
                val content = message.getString("content")

                val result = Json.decodeFromString<DecisionAnalysisResult>(content)
                return result
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun suggest(systemPrompt: String, prompt: String): List<CriterionSuggestion>? {

        val mediaType = "application/json; charset=utf-8".toMediaType()

        val systemPrompt = JSONObject().apply {
            put("role", "system")
            put("content", systemPrompt)
        }

        val userPrompt = JSONObject().apply {
            put("role", "user")
            put("content", prompt)
        }

        val promptsArray = JSONArray().apply {
            put(systemPrompt)
            put(userPrompt)
        }

        val jsonRequestBody = JSONObject().apply {
            put("model", "gpt-4.1-mini")
            put("messages", promptsArray)
            put("temperature", 0.2)

        }.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", System.getenv("OPENAI_KEY"))
            .post(jsonRequestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: ""
                val responseJson = JSONObject(responseBody)
                val choicesArray = responseJson.getJSONArray("choices")
                val choice = choicesArray.getJSONObject(0)
                val message = choice.getJSONObject("message")
                val content = message.getString("content")

                val result = Json.decodeFromString<List<CriterionSuggestion>>(content)
                return result
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun dailyArticle(systemPrompt: String, prompt: String): DailyArticle? {
        val mediaType = "application/json; charset=utf-8".toMediaType()

        val systemPrompt = JSONObject().apply {
            put("role", "system")
            put("content", systemPrompt)
        }

        val userPrompt = JSONObject().apply {
            put("role", "user")
            put("content", prompt)
        }

        val promptsArray = JSONArray().apply {
            put(systemPrompt)
            put(userPrompt)
        }

        val jsonRequestBody = JSONObject().apply {
            put("model", "gpt-4.1-mini")
            put("messages", promptsArray)
            put("temperature", 0.4)

        }.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", System.getenv("OPENAI_KEY"))
            .post(jsonRequestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: ""
                val responseJson = JSONObject(responseBody)
                val choicesArray = responseJson.getJSONArray("choices")
                val choice = choicesArray.getJSONObject(0)
                val message = choice.getJSONObject("message")
                val content = message.getString("content")

                val result = Json.decodeFromString<DailyArticle>(content)
                return result
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun safetyClassification(systemPrompt: String, prompt: String): SafetyClassification? {
        val mediaType = "application/json; charset=utf-8".toMediaType()

        val systemPrompt = JSONObject().apply {
            put("role", "system")
            put("content", systemPrompt)
        }

        val userPrompt = JSONObject().apply {
            put("role", "user")
            put("content", prompt)
        }

        val promptsArray = JSONArray().apply {
            put(systemPrompt)
            put(userPrompt)
        }

        val jsonRequestBody = JSONObject().apply {
            put("model", "gpt-4.1-mini")
            put("messages", promptsArray)
            put("temperature", 0.2)

        }.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", System.getenv("OPENAI_KEY"))
            .post(jsonRequestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: ""
                val responseJson = JSONObject(responseBody)
                val choicesArray = responseJson.getJSONArray("choices")
                val choice = choicesArray.getJSONObject(0)
                val message = choice.getJSONObject("message")
                val content = message.getString("content")

                val result = Json.decodeFromString<SafetyClassification>(content)
                return result
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}