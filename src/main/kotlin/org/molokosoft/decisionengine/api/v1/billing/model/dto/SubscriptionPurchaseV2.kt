package org.molokosoft.decisionengine.api.v1.billing.model.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class SubscriptionPurchaseV2(
    val kind: String? = null,
    val startTime: String? = null,
    val regionCode: String? = null,
    val subscriptionState: String,
    val latestOrderId: String? = null,
    val testPurchase: JsonObject? = null,
    val acknowledgementState: String? = null,
    val lineItems: List<SubscriptionLineItem> = emptyList()
)
