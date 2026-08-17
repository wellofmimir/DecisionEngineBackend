package org.molokosoft.decisionengine.api.v1.billing.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductPurchase(
    val kind: String? = null,
    val purchaseTimeMillis: String? = null,
    val purchaseState: Int? = null,
    val consumptionState: Int? = null,
    val developerPayload: String? = null,
    val orderId: String? = null,
    val purchaseType: Int? = null,
    val acknowledgementState: Int? = null,
    val purchaseToken: String? = null,
    val productId: String? = null,
    val quantity: Int? = null,
    val obfuscatedExternalAccountId: String? = null,
    val obfuscatedExternalProfileId: String? = null,
    val regionCode: String? = null,
    val refundableQuantity: Int? = null
)
