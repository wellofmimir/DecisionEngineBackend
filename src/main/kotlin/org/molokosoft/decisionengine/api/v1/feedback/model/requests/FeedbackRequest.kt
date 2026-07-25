package org.molokosoft.decisionengine.api.v1.feedback.model.requests

import kotlinx.serialization.Serializable
import org.molokosoft.decisionengine.api.v1.model.Validatable
import org.molokosoft.decisionengine.exceptions.BadRequestException

@Serializable
data class FeedbackRequest(
    val feedback: String
) : Validatable {
    override fun validate() {
        if (feedback.length > 2000)
            throw BadRequestException("Feedback has to many letters.")
    }
}