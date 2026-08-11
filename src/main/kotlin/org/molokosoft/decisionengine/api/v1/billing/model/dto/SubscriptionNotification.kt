package org.molokosoft.decisionengine.api.v1.billing.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionNotification(
    val version: String? = null,
    val notificationType: Int,
    val purchaseToken: String,
    val subscriptionId: String? = null
)
