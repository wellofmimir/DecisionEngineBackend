package org.molokosoft.decisionengine.api.v1.billing.model.requests

import kotlinx.serialization.Serializable
import org.molokosoft.decisionengine.api.v1.billing.model.dto.PubSubMessage

@Serializable
data class PubSubPushRequest(
    val message: PubSubMessage,
    val subscription: String
)
