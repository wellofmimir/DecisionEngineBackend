package org.molokosoft.decisionengine.api.v1.billing.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class OfferDetails(
    val basePlanId: String? = null,
    val offerTags: List<String> = emptyList()
)