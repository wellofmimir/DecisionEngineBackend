package org.molokosoft.decisionengine.services.email

import kotlinx.serialization.Serializable

@Serializable
data class Recipient(
    val eMail: String,
    val name: String
)
