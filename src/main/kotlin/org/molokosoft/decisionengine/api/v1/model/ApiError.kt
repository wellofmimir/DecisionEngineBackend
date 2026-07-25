package org.molokosoft.decisionengine.api.v1.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val code: String,
    val message: String
)