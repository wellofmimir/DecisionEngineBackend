package org.molokosoft.decisionengine.api.v1.billing.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionLineItem(
    val productId: String,
    val expiryTime: String,
    val autoRenewingPlan: AutoRenewingPlan? = null,
    val offerDetails: OfferDetails? = null
)

