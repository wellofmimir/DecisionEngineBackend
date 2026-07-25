package org.molokosoft.decisionengine.services.criteria

import org.molokosoft.decisionengine.ai.v1.clients.AiClient
import org.molokosoft.decisionengine.ai.v1.prompts.PromptBuilder
import org.molokosoft.decisionengine.api.v1.criteria.model.dto.CriterionSuggestion
import org.molokosoft.decisionengine.api.v1.criteria.model.requests.CriteriaSuggestionRequest

class CriteriaService(
    private val aiClient: AiClient,
    private val promptBuilder: PromptBuilder
) {
    suspend fun suggest(request: CriteriaSuggestionRequest): List<CriterionSuggestion>? {
        val prompt = promptBuilder.buildCriteriaPrompt(request)
        return aiClient.suggest(promptBuilder.systemPromptCriteriaSuggestion, prompt)
    }
}