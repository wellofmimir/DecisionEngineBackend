package org.molokosoft.decisionengine.services.security

import org.molokosoft.decisionengine.ai.v1.clients.AiClient
import org.molokosoft.decisionengine.ai.v1.prompts.PromptBuilder
import org.molokosoft.decisionengine.api.v1.security.model.dto.PromptReconnaissanceResult
import org.molokosoft.decisionengine.api.v1.security.model.requests.PromptReconnaissanceRequest

class SecurityService(
    private val aiClient: AiClient,
    private val promptBuilder: PromptBuilder
) {
    suspend fun promptReconnaissance(promptReconnaissanceRequest: PromptReconnaissanceRequest): PromptReconnaissanceResult? {
        val prompt = promptBuilder.buildPromptReconnaissancePrompt(promptReconnaissanceRequest)
        return aiClient.promptReconnaissance(promptBuilder.systemPromptPromptReconnaissance, prompt)
    }
}
