package org.molokosoft.decisionengine.services.decision

import org.molokosoft.decisionengine.ai.v1.clients.AiClient
import org.molokosoft.decisionengine.api.v1.decision.model.dto.DecisionAnalysisResult
import org.molokosoft.decisionengine.api.v1.decision.model.requests.DecisionAnalysisRequest
import org.molokosoft.decisionengine.ai.v1.prompts.PromptBuilder

class DecisionService(
    private val aiClient: AiClient,
    private val promptBuilder: PromptBuilder
) {
    suspend fun analyze(request: DecisionAnalysisRequest): DecisionAnalysisResult? {
        val prompt = promptBuilder.buildAnalysisPrompt(request)
        return aiClient.analyze( promptBuilder.systemPromptDecisionAnalysis, prompt)
    }
}