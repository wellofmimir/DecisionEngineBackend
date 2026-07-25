package org.molokosoft.decisionengine.api.v1.criteria.model.dto

import kotlinx.serialization.Serializable
import org.molokosoft.decisionengine.api.v1.model.Validatable

@Serializable
data class CriterionSuggestion(
    val name: String,
    val description: String
) : Validatable {
    override fun validate() {
    }
}
