package org.molokosoft.decisionengine.api.v1.billing.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class VoidedPurchaseNotification(
    val purchaseToken: String? = null,
    val orderId: String? = null,
    val productType: Int? = null,
    val refundType: Int? = null
)
