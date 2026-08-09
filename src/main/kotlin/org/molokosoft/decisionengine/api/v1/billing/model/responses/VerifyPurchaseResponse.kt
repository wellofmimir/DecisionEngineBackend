package org.molokosoft.decisionengine.api.v1.billing.model.responses

import kotlinx.serialization.Serializable

@Serializable
data class VerifyPurchaseResponse(
    val apiKey: String
)
