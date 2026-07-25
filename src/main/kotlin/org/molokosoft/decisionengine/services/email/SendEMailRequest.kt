package org.molokosoft.decisionengine.services.email

import kotlinx.serialization.Serializable

@Serializable
data class SendEMailRequest(
    val sender: Sender,
    val to: List<Recipient>,
    val subject: String,
    val htmlContent: String
)
