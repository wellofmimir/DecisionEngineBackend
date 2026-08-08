package org.molokosoft.decisionengine.api.v1.decision.model.responses

import kotlinx.serialization.Serializable
import org.molokosoft.decisionengine.api.v1.decision.model.dto.SafetyClassification

@Serializable
data class SafetyClassificationResponse(
    val safetyClassification: SafetyClassification
)
