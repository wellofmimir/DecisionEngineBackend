package org.molokosoft.decisionengine.api.v1.decision.model.requests

import kotlinx.serialization.Serializable
import org.molokosoft.decisionengine.api.v1.model.Validatable
import org.molokosoft.decisionengine.exceptions.BadRequestException

@Serializable
data class SafetyClassificationRequest(
    val title: String
) : Validatable {
    override fun validate() {
        title.ifBlank {
            throw BadRequestException("Decision-Title must not be blank.")
        }
    }
}