package org.molokosoft.decisionengine.repositories.users

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.molokosoft.decisionengine.database.tables.apikeys.ApiKey
import org.molokosoft.decisionengine.database.tables.apikeys.ApiKeys
import org.molokosoft.decisionengine.database.tables.purchases.Purchases
import org.molokosoft.decisionengine.database.tables.users.User
import org.molokosoft.decisionengine.database.tables.users.Users

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

enum class ConsumedUsage {
    Subscription,
    Consumable
}

class UserRepository {

    @OptIn(ExperimentalTime::class)
    fun create(eMail: String): Int = transaction {
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

    fun restoreUsage(apiKeyHash: String, usage: ConsumedUsage) {
        when (usage) {
            ConsumedUsage.Consumable ->
                addConsumableUsages(1, apiKeyHash)

            ConsumedUsage.Subscription ->
                addSubscriptionUsages(1, apiKeyHash)
        }
    }

    fun consumeUsage(apiKeyHash: String): ConsumedUsage? {
        if (decrementSubscriptionUsages(apiKeyHash))
            return ConsumedUsage.Subscription

        if (decrementConsumableUsages(apiKeyHash))
            return ConsumedUsage.Consumable

        return null
    }

    private fun decrementSubscriptionUsages(apiKeyHash: String): Boolean {
        val updatedRows =
            ApiKeys
                .update(
                  where = {
                      (ApiKeys.apiKeyHash eq apiKeyHash) and (ApiKeys.subscriptionsUsages greater 0)
                  }
               ) {
                  with (SqlExpressionBuilder) {
                      it[subscriptionsUsages] = subscriptionsUsages - 1
                  }
              }

        return updatedRows == 1
    }

    private fun decrementConsumableUsages(apiKeyHash: String): Boolean {
        val updatedRows =
            ApiKeys
                .update(
                    where = {
                      (ApiKeys.apiKeyHash eq apiKeyHash) and (ApiKeys.consumableUsages greater 0)
                    }
                ) {
                    with (SqlExpressionBuilder) {
                        it[consumableUsages] = consumableUsages - 1
                    }
                }

        return updatedRows == 1
    }

    fun addSubscriptionUsages(
        usages: Int,
        apiKeyHash: String
    ) {
        ApiKeys
            .update(
                where = {
                    ApiKeys.apiKeyHash eq apiKeyHash
                }
            ) {
                with (SqlExpressionBuilder) {
                    it[subscriptionsUsages] = subscriptionsUsages + usages
                }
            }
    }

    fun addConsumableUsages(
        usages: Int,
        apiKeyHash: String
    ) {
        ApiKeys
            .update(
                where = {
                    ApiKeys.apiKeyHash eq apiKeyHash
                }
            ) {
                with (SqlExpressionBuilder) {
                    it[consumableUsages] = consumableUsages + usages
                }
            }
    }

    fun purchaseExists(purchaseToken: String): Boolean = transaction {
        Purchases
            .selectAll()
            .where {
                Purchases.purchaseToken eq purchaseToken
            }
            .limit(1)
            .any()
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

    fun findApiKey(apiKeyHash: String): ApiKey? = transaction {
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
    fun insertApiKey(apiKeyHash: String, purchaseToken: String, expiresAt: Long?) = transaction {
        ApiKeys.insert {
            it[ApiKeys.apiKeyHash] = apiKeyHash
            it[ApiKeys.subscriptionsUsages] = 0
            it[ApiKeys.consumableUsages] = 0
            it[ApiKeys.isActive] = false
            it[ApiKeys.expiresAt] = expiresAt
        }

        val apiKeyId =
            ApiKeys
                .selectAll()
                .where {
                    ApiKeys.apiKeyHash eq apiKeyHash
                }
                .single()[ApiKeys.id]

        Purchases.insert {
            it[Purchases.purchaseToken] = purchaseToken
            it[Purchases.apiKeyId] = apiKeyId
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

    fun insertPurchaseToken(purchaseToken: String, apiKeyId: Int) = transaction {
        Purchases
            .insert {
                it[Purchases.purchaseToken] = purchaseToken
                it[Purchases.apiKeyId] = apiKeyId
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
        subscriptionUsages = this[ApiKeys.subscriptionsUsages],
        consumableUsages = this[ApiKeys.consumableUsages],
        isActive = this[ApiKeys.isActive]
    )
}