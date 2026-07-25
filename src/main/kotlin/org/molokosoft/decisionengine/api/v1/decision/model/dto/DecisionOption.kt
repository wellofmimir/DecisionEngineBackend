package org.molokosoft.decisionengine.api.v1.decision.model.dto

import kotlinx.serialization.Serializable
import org.molokosoft.decisionengine.api.v1.model.ApiLimits
import org.molokosoft.decisionengine.api.v1.model.Validatable
import org.molokosoft.decisionengine.exceptions.BadRequestException
import org.molokosoft.decisionengine.extensions.hasDuplicatesBy

@Serializable
data class DecisionOption(
    val name: String,
    val overallScore: Double,
    val reversibility: Int,
    val criteria: List<DecisionCriterion>
) : Validatable {

    override fun validate() {
        if (name.isBlank())
            throw BadRequestException("Option-Name must not be blank.")

        if (name.length > 100)
            throw BadRequestException("Option-Name must be <= 100 characters.")

        if (overallScore.toFloat() !in 0.0f .. 10.0f)
            throw BadRequestException("Option-Score must be in the range of 0.0f <= score <= 10.0f.")

        if (reversibility !in 1 .. 10)
            throw BadRequestException("Option-Reversibility must be in the range of 1 <= reversibility <= 10.")

        if (criteria.isEmpty())
            throw BadRequestException("Option-Criteria may not be empty.")

        if (criteria.size > ApiLimits.MAX_CRITERIA_PER_OPTION)
            throw BadRequestException("An Option may not have more than ${ApiLimits.MAX_CRITERIA_PER_OPTION} criteria.")

        if (criteria.hasDuplicatesBy { it.name })
            throw BadRequestException("Every criterion should be unique.")

        criteria.forEach {
            it.validate()
        }
    }
}
