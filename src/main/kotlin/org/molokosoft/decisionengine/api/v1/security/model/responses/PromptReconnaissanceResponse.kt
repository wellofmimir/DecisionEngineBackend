package org.molokosoft.decisionengine.api.v1.security.model.responses

import kotlinx.serialization.Serializable
import org.molokosoft.decisionengine.api.v1.security.model.dto.PromptReconnaissanceResult

@Serializable
data class PromptReconnaissanceResponse(
    val result: PromptReconnaissanceResult
)
