package org.molokosoft.decisionengine.ai.v1.prompts

import org.molokosoft.decisionengine.api.v1.criteria.model.requests.CriteriaSuggestionRequest
import org.molokosoft.decisionengine.api.v1.decision.model.requests.DecisionAnalysisRequest
import org.molokosoft.decisionengine.api.v1.decision.model.requests.SafetyClassificationRequest

interface PromptBuilder {
    val systemPromptDecisionAnalysis: String
    val systemPromptCriteriaSuggestion: String
    val systemPromptDailyArticle: String
    val systemPromptSafetyClassification: String

    fun buildAnalysisPrompt(request: DecisionAnalysisRequest): String
    fun buildCriteriaPrompt(request: CriteriaSuggestionRequest): String
    fun buildSafetyClassifier(request: SafetyClassificationRequest): String
    fun buildDailyArticlePrompt(topic: String): String
}