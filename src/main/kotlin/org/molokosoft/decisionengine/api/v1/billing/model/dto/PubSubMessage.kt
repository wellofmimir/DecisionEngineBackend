package org.molokosoft.decisionengine.api.v1.billing.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class PubSubMessage(
    val data: String,
    val messageId: String,
    val publishTime: String? = null
)