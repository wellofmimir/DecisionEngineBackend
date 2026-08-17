package org.molokosoft.decisionengine.api.v1.billing.model

enum class Product(
    val productId: String,
    val type: ProductType,
    val usageLimit: Int
) {
    WEEKLY_SUBSCRIPTION(
        productId = "decisionengine_weekly_subscription",
        type = ProductType.SUBSCRIPTION,
        usageLimit = 20
    ),

    DECISION_PACK_15(
        productId = "decisionengine_pack_15",
        type = ProductType.CONSUMABLE,
        usageLimit = 15
    )
}

enum class ProductType {
    SUBSCRIPTION,
    CONSUMABLE
}