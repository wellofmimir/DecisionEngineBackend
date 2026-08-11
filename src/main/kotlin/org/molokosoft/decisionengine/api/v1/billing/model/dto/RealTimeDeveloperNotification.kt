package org.molokosoft.decisionengine.api.v1.billing.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class RealTimeDeveloperNotification(
    val version: String? = null,
    val packageName: String? = null,
    val eventTimeMillis: String? = null,
    val subscriptionNotification: SubscriptionNotification? = null,
    val oneTimeProductNotification: OneTimeProductNotification? = null,
    val voidedPurchaseNotification: VoidedPurchaseNotification? = null,
    val testNotification: TestNotification? = null
)
