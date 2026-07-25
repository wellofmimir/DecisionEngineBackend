package org.molokosoft.decisionengine.api.v1.criteria.model.requests

import kotlinx.serialization.Serializable
import org.molokosoft.decisionengine.api.v1.model.Validatable

@Serializable
data class CriteriaSuggestionRequest(
    val decisionTitle: String
) : Validatable {
    override fun validate() {
    }
}