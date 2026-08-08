package org.molokosoft.decisionengine.api.v1.decision.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class SafetyClassification(
    val classification: String,
    val reason: String
)
