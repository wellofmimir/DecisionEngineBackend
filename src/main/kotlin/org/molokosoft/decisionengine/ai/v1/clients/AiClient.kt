package org.molokosoft.decisionengine.ai.v1.clients

import org.molokosoft.decisionengine.api.v1.articles.model.dto.DailyArticle
import org.molokosoft.decisionengine.api.v1.criteria.model.dto.CriterionSuggestion
import org.molokosoft.decisionengine.api.v1.decision.model.dto.DecisionAnalysisResult

interface AiClient {
    suspend fun analyze(systemPrompt: String, prompt: String): DecisionAnalysisResult?
    suspend fun suggest(systemPrompt: String, prompt: String): List<CriterionSuggestion>?
    suspend fun dailyArticle(systemPrompt: String, prompt: String): DailyArticle?
}