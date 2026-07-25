package org.molokosoft.decisionengine.api.v1.decision.model.dto

import kotlinx.serialization.Serializable
import org.molokosoft.decisionengine.api.v1.model.Validatable
import org.molokosoft.decisionengine.exceptions.BadRequestException

@Serializable
data class DecisionCriterion(
    val name: String,
    val importance: Int,
    val score: Int
) : Validatable {

    override fun validate() {
        if (name.isBlank())
            throw BadRequestException("Criterion-Name may not be empty.")

        if (name.length > 100)
            throw BadRequestException("Option-Name must be <= 100 characters.")

        if (importance !in 1 .. 10)
            throw BadRequestException("Criterion-Importance must be in the range 1 <= importance <= 10.")

        if (score !in 1 .. 10)
            throw BadRequestException("Criterion-Score must be in the range 1 <= score <= 10.")
    }
}
