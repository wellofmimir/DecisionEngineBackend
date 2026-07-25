package org.molokosoft.decisionengine.api.v1.decision.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class DecisionAnalysisResult(
    val summary: String,
    val recommendedOption: String,
    val whyItStandsOut: String,
    val reversibility: String,
    val blindSpots: String,
    val roadmapToSuccess: String,
    val conclusion: String,
    val category: String
)
