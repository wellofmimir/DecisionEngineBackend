package org.molokosoft.decisionengine.api.v1.criteria.model.responses

import kotlinx.serialization.Serializable
import org.molokosoft.decisionengine.api.v1.criteria.model.dto.CriterionSuggestion
import org.molokosoft.decisionengine.api.v1.model.Validatable

@Serializable
data class CriteriaSuggestionResponse(
    val criteria: List<CriterionSuggestion>
) : Validatable {
    override fun validate() {
    }
}