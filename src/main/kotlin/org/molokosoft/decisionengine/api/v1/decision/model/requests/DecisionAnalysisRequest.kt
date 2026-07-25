package org.molokosoft.decisionengine.api.v1.decision.model.requests

import org.molokosoft.decisionengine.api.v1.decision.model.dto.DecisionOption
import kotlinx.serialization.Serializable
import org.molokosoft.decisionengine.api.v1.model.ApiLimits
import org.molokosoft.decisionengine.api.v1.model.Validatable
import org.molokosoft.decisionengine.exceptions.BadRequestException
import org.molokosoft.decisionengine.extensions.hasDuplicatesBy

@Serializable
data class DecisionAnalysisRequest(
    val decisionTitle: String,
    val recommendedOption: String,
    val options: List<DecisionOption>
) : Validatable {

    override fun validate() {
        if (decisionTitle.isBlank())
            throw BadRequestException("Decision-Title must not be blank.")

        if (decisionTitle.length > 200)
            throw BadRequestException("Decision-Title must be <= 200 characters.")

        if (options.isEmpty())
            throw BadRequestException("Options may not be empty.")

        if (options.size < ApiLimits.MIN_OPTIONS)
            throw BadRequestException("There must be at least two options.");

        if (options.size > ApiLimits.MAX_OPTIONS)
            throw BadRequestException("There must be not more than ten options.");

        if (options.hasDuplicatesBy { it.name })
            throw BadRequestException("Every option should be unique.")

        options.forEach {
            it.validate()
        }

        val totalCharacters =
                    decisionTitle.length +
                    recommendedOption.length +
                    options.sumOf { option ->
                        option.name.length +
                        option.reversibility.toString().length +
                        option.overallScore.toString().length +
                        option.criteria.sumOf {
                            it.name.length
                        }
                    }

        if (totalCharacters > ApiLimits.MAX_TOTAL_LENGTH_TEXT)
            throw BadRequestException("Total amount of characters in the request must be <= ${ApiLimits.MAX_TOTAL_LENGTH_TEXT}")
    }
}