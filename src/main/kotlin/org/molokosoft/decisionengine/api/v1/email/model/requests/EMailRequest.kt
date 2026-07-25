package org.molokosoft.decisionengine.api.v1.email.model.requests

import kotlinx.serialization.Serializable
import org.molokosoft.decisionengine.model.EMail
import org.molokosoft.decisionengine.api.v1.model.Validatable
import org.molokosoft.decisionengine.exceptions.BadRequestException

@Serializable
data class EMailRequest(
    val eMail: String
) : Validatable {
    override fun validate() {
        EMail.tryCreate(eMail) ?:
            throw BadRequestException("Input does not match an E-Mail-Address.")
    }
}