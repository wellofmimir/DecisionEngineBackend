package org.molokosoft.decisionengine.api.v1.quote.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    val quote: String,
    val person: String
)

