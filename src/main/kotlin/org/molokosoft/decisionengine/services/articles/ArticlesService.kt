package org.molokosoft.decisionengine.services.articles

import org.molokosoft.decisionengine.ai.v1.clients.AiClient
import org.molokosoft.decisionengine.ai.v1.prompts.PromptBuilder
import org.molokosoft.decisionengine.api.v1.articles.model.dto.DailyArticle

class ArticlesService(
    private val aiClient: AiClient,
    private val promptBuilder: PromptBuilder
) {
    suspend fun dailyArticle(topic: String): DailyArticle? {
        val prompt = promptBuilder.buildDailyArticlePrompt(topic)
        return aiClient.dailyArticle(promptBuilder.systemPromptDailyArticle, prompt)
    }
}