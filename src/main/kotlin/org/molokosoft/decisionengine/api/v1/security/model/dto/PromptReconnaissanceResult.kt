package org.molokosoft.decisionengine.api.v1.security.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class PromptReconnaissanceResult(
    val isPrompt: Boolean,
    val reason: String
)
