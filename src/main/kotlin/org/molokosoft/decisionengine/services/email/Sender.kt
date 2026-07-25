package org.molokosoft.decisionengine.services.email

import kotlinx.serialization.Serializable

@Serializable
data class Sender(
    val name: String,
    val eMail: String
)
