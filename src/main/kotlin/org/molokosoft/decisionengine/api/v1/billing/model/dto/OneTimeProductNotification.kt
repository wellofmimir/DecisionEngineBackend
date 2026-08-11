package org.molokosoft.decisionengine.api.v1.billing.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class OneTimeProductNotification(
    val version: String? = null,
    val notificationType: Int? = null,
    val purchaseToken: String? = null,
    val sku: String? = null
)
