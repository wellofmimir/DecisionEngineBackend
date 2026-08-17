package org.molokosoft.decisionengine.api.v1.billing.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class AutoRenewingPlan(
    val autoRenewEnabled: Boolean? = null
)