package org.molokosoft.decisionengine.ai.v1.prompts

import org.molokosoft.decisionengine.api.v1.criteria.model.requests.CriteriaSuggestionRequest
import org.molokosoft.decisionengine.api.v1.decision.model.requests.DecisionAnalysisRequest

interface PromptBuilder {
    val systemPromptDecisionAnalysis: String
    val systemPromptCriteriaSuggestion: String
    val systemPromptDailyArticle: String

    fun buildAnalysisPrompt(request: DecisionAnalysisRequest): String
    fun buildCriteriaPrompt(request: CriteriaSuggestionRequest): String
    fun buildDailyArticlePrompt(topic: String): String
}