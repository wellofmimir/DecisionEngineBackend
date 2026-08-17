package org.molokosoft.decisionengine.api.v1.billing.model.requests

import kotlinx.serialization.Serializable
import org.molokosoft.decisionengine.api.v1.billing.model.Product
import org.molokosoft.decisionengine.api.v1.model.Validatable
import org.molokosoft.decisionengine.exceptions.BadRequestException

@Serializable
data class VerifyPurchaseRequest(
    val purchaseToken: String,
    val productId: String,
    val apiKey: String? = null
): Validatable {
    override fun validate() {
        if (purchaseToken.isBlank())
            throw BadRequestException("Purchase-Token is blank.")

        if (purchaseToken.isBlank())
            throw BadRequestException("Product-ID is blank.")

        Product.entries.firstOrNull {
            it.productId == productId
        } ?: throw BadRequestException("Unknown Product-ID")
    }
}