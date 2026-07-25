package org.molokosoft.decisionengine.api.v1.billing.model.requests

import kotlinx.serialization.Serializable
import org.molokosoft.decisionengine.api.v1.model.Validatable
import org.molokosoft.decisionengine.exceptions.BadRequestException

@Serializable
data class VerifyPurchaseRequest(
    val purchaseToken: String
): Validatable {
    override fun validate() {
        if (purchaseToken.isBlank())
            throw BadRequestException("Purchase-Token is blank.")
    }
}