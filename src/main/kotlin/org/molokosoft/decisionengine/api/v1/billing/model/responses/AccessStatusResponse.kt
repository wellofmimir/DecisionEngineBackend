package org.molokosoft.decisionengine.api.v1.billing.model.responses

import kotlinx.serialization.Serializable
import org.molokosoft.decisionengine.api.v1.billing.model.dto.AccessStatus

@Serializable
data class AccessStatusResponse(
    val accessStatus: AccessStatus
)
