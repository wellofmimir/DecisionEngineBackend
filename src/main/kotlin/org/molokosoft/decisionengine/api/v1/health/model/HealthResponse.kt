package org.molokosoft.decisionengine.api.v1.health.model

import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    val status: String
)
