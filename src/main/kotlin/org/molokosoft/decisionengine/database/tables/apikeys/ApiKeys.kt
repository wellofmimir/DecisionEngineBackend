package org.molokosoft.decisionengine.database.tables.apikeys

import org.jetbrains.exposed.sql.Table

object ApiKeys : Table("ApiKeys") {

    val id = integer("id").autoIncrement()
    val apiKeyHash = varchar("apiKeyHash", 64).uniqueIndex()
    val subscriptionsUsages = integer("subscriptionUsages")
    val consumableUsages = integer("consumableUsages")
    val expiresAt = long("expiresAt").nullable()
    val isActive = bool("isActive")

    override val primaryKey = PrimaryKey(id)
}