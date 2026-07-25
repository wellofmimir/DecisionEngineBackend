package org.molokosoft.decisionengine.api.v1.billing.model.responses

import kotlinx.serialization.Serializable
import org.molokosoft.decisionengine.api.v1.model.Validatable

@Serializable
data class VerifyPurchaseResponse(
    val apiKey: String
) : Validatable {
    override fun validate() {

    }
}
