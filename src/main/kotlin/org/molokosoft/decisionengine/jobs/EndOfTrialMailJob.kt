package org.molokosoft.decisionengine.jobs

import org.molokosoft.decisionengine.model.EMail
import org.molokosoft.decisionengine.repositories.users.UserRepository
import org.molokosoft.decisionengine.services.email.EMailService

class EndOfTrialMailJob(
    private val userRepository: UserRepository,
    private val eMailService: EMailService
) : Job {
    override suspend fun execute() {
        val users = userRepository.findUsersWhereTrialIsOver()

        users.forEach {
            val eMail = EMail(it.eMail)

            if (eMailService.sendEndOfTrialNotification(eMail))
                userRepository.updateUser(it.eMail)
        }
    }
}