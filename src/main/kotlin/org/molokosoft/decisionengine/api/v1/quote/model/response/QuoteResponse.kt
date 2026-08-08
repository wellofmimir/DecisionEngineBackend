package org.molokosoft.decisionengine.api.v1.quote.model.response

import org.molokosoft.decisionengine.api.v1.quote.model.dto.Quote
import kotlinx.serialization.Serializable

@Serializable
data class QuoteResponse(
    val quote: Quote
)
