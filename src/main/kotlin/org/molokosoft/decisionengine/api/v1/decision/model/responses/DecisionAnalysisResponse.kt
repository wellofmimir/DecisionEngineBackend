package org.molokosoft.decisionengine.api.v1.decision.model.responses

import kotlinx.serialization.Serializable
import org.molokosoft.decisionengine.api.v1.decision.model.dto.DecisionAnalysisResult

@Serializable
data class DecisionAnalysisResponse(
    val result: DecisionAnalysisResult
)