package org.molokosoft.decisionengine.repositories.users

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.molokosoft.decisionengine.database.tables.apikeys.ApiKey
import org.molokosoft.decisionengine.database.tables.apikeys.ApiKeys
import org.molokosoft.decisionengine.database.tables.users.User
import org.molokosoft.decisionengine.database.tables.users.Users

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class UserRepository {

    @OptIn(ExperimentalTime::class)
    fun create(eMail: String): Int = transaction {
        val now = Clock.System.now()

        val insert = Users.insert {
            it[Users.eMail] = eMail
            it[trialStarted] = Clock.System.now().toEpochMilliseconds()
            it[trialEnds] = Clock.System.now().toEpochMilliseconds()
            it[reminderSent] = false
        }

        insert[Users.id]
    }

    @OptIn(ExperimentalTime::class)
    fun findUsersWhereTrialIsOver(): List<User> = transaction {
        val now = Clock.System.now().toEpochMilliseconds()

        Users
            .selectAll()
            .where {
                Users.trialEnds less now and (Users.reminderSent eq false)
            }
            .map {
                it.toUser()
            }
    }

    fun updateUser(eMail: String) = transaction {
        Users.update(
            where = {
                Users.eMail eq eMail
            }
        ) {
            it[reminderSent] = true
        }
    }

    fun findApiKeyHash(apiKeyHash: String): ApiKey? = transaction {
        ApiKeys
            .selectAll()
            .where {
                ApiKeys.apiKeyHash eq apiKeyHash
            }
            .singleOrNull()?.toApiKey()
    }

    fun insertApiKey(apiKeyHash: String) = transaction {
        ApiKeys.insert {
            it[ApiKeys.apiKeyHash] = apiKeyHash
            it[ApiKeys.isActive] = false
        }
    }

    @OptIn(ExperimentalTime::class)
    fun insertApiKey(apiKeyHash: String, purchaseToken: String, usages: Int, expiresAt: Long) = transaction {
        ApiKeys.insert {
            it[ApiKeys.apiKeyHash] = apiKeyHash
            it[ApiKeys.purchaseToken] = purchaseToken
            it[ApiKeys.remainingUsages] = usages
            it[ApiKeys.isActive] = false
            it[ApiKeys.expiresAt] = expiresAt
        }
    }

    fun updateApiKeyUsages(apiKeyHash: String, usages: Int) = transaction {
        ApiKeys.update (
            where = {
                ApiKeys.apiKeyHash eq apiKeyHash
            }
        ) {
            it[remainingUsages] = usages
        }
    }

    fun activateApiKey(apiKeyHash: String) = transaction {
        ApiKeys.update(
            where = {
                ApiKeys.apiKeyHash eq apiKeyHash
            }
        ) {
            it[ApiKeys.isActive] = true
        }
    }

    fun deactivateApiKey(apiKeyHash: String) = transaction {
        ApiKeys.update(
            where = {
                ApiKeys.apiKeyHash eq apiKeyHash
            }
        ) {
            it[ApiKeys.isActive] = false
        }
    }

    fun findByEMail(eMail: String): User? = transaction {
        Users.selectAll()
            .where {
                Users.eMail eq eMail
            }
            .singleOrNull()?.toUser()
    }

    private fun ResultRow.toUser() = User(
        id = this[Users.id],
        eMail = this[Users.eMail],
        trialStarted = this[Users.trialStarted],
        trialEnds = this[Users.trialEnds],
        reminderSent = this[Users.reminderSent]
    )

    private fun ResultRow.toApiKey() = ApiKey(
        id = this[ApiKeys.id],
        apiKeyHash = this[ApiKeys.apiKeyHash],
        remainingUsages = this[ApiKeys.remainingUsages],
        isActive = this[ApiKeys.isActive]
    )
}