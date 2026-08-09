package org.molokosoft.decisionengine.ai.v1.clients

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import org.json.JSONArray
import org.json.JSONObject
import org.molokosoft.decisionengine.api.v1.articles.model.dto.DailyArticle
import org.molokosoft.decisionengine.api.v1.criteria.model.dto.CriterionSuggestion
import org.molokosoft.decisionengine.api.v1.decision.model.dto.DecisionAnalysisResult
import org.molokosoft.decisionengine.api.v1.decision.model.dto.SafetyClassification

class OpenAiClient(
    private val client: OkHttpClient
) : AiClient {

    private suspend fun createOpenAiRequestBody(
         systemPrompt: String,
         prompt: String,
         temperature: Double
    ): RequestBody {
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
            put("temperature", temperature)

        }.toString().toRequestBody(mediaType)

        return jsonRequestBody
    }

    private suspend fun requestOpenAi(
        systemPrompt: String,
        prompt: String,
        temperature: Double
    ): String? = withContext(Dispatchers.IO) {

        val requestBody = createOpenAiRequestBody(
            systemPrompt = systemPrompt,
            prompt = prompt,
            temperature = temperature
        )

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", System.getenv("OPENAI_KEY"))
            .post(requestBody)
            .build()

        val start = System.currentTimeMillis()

        try {
            client.newCall(request).execute().use { response ->

                val duration = System.currentTimeMillis() - start
                val responseBody = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    return@withContext null
                }

                val responseJson = JSONObject(responseBody)

                return@withContext responseJson
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
            }

        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - start
            return@withContext null
        }
    }

    override suspend fun analyze(systemPrompt: String, prompt: String): DecisionAnalysisResult? {

        val content = requestOpenAi(
            systemPrompt = systemPrompt,
            prompt = prompt,
            temperature = 0.2,
        ) ?: return null

        return try {
            Json.decodeFromString<DecisionAnalysisResult>(content)

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun suggest(systemPrompt: String, prompt: String): List<CriterionSuggestion>? {

        val content = requestOpenAi(
            systemPrompt = systemPrompt,
            prompt = prompt,
            temperature = 0.2,
        ) ?: return null

        return try {
            Json.decodeFromString<List<CriterionSuggestion>>(content)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun dailyArticle(systemPrompt: String, prompt: String): DailyArticle? {

        val content = requestOpenAi(
            systemPrompt = systemPrompt,
            prompt = prompt,
            temperature = 0.2,
        ) ?: return null

        return try {
            Json.decodeFromString<DailyArticle>(content)

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun safetyClassification(systemPrompt: String, prompt: String): SafetyClassification? {

        val content = requestOpenAi(
            systemPrompt = systemPrompt,
            prompt = prompt,
            temperature = 0.2,
        ) ?: return null

        return try {
            Json.decodeFromString<SafetyClassification>(content)

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}