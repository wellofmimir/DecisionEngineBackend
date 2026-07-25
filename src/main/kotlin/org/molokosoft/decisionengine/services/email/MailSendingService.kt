package org.molokosoft.decisionengine.services.email

interface MailSendingService {
    fun send(to: String, subject: String, text: String)
}