package org.molokosoft.decisionengine.database.tables.apikeys

import org.jetbrains.exposed.sql.Table

object ApiKeys : Table("ApiKeys") {

    val id = integer("id").autoIncrement()
    val apiKeyHash = varchar("apiKeyHash", 64).uniqueIndex()
    val purchaseToken = varchar("purchaseToken", 512).uniqueIndex()
    val remainingUsages = integer("remainingUsages")
    val expiresAt = long("expiresAt")
    val isActive = bool("isActive")

    override val primaryKey = PrimaryKey(id)
}