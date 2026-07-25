package org.molokosoft.decisionengine.services.email

import org.molokosoft.decisionengine.model.EMail
import org.molokosoft.decisionengine.repositories.users.UserRepository
import org.molokosoft.decisionengine.services.email.clients.EMailClient

import java.io.File

class EMailService(
    private val userRepository: UserRepository,
    private val eMailClient: EMailClient
) {
    fun save(eMail: EMail) {
        if (userRepository.findByEMail(eMail.toString()) == null)
            userRepository.create(eMail.toString())
    }

    fun sendEndOfTrialNotification(eMail: EMail): Boolean {
        val fileWithHTMLDesign = File("end_of_trial_design.txt")
        val htmlDesign = fileWithHTMLDesign.readText()

        val request = SendEMailRequest(
            sender = Sender(
                name = "Patryk from DecisionEngine",
                eMail = "mleczko.patryk.roman@gmail.com"
            ),
            to = listOf(
                Recipient(
                    eMail = eMail.toString(),
                    name = "Patryk Roman Mleczko"
                )
            ),
            subject = "DecisionEngine: Free Trial has ended.",
            htmlContent = htmlDesign.trimIndent()
        )

        return eMailClient.sendEMail(request)
    }
}