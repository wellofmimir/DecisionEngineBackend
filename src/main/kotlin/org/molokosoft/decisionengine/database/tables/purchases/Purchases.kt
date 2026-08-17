package org.molokosoft.decisionengine.database.tables.purchases

import org.jetbrains.exposed.sql.Table

import org.molokosoft.decisionengine.database.tables.apikeys.ApiKeys

object Purchases : Table("Purchases") {

    val id =
        integer("id").autoIncrement()

    val purchaseToken =
        varchar("purchaseToken", 512).uniqueIndex()

    val apiKeyId =
        integer("apiKeyId")
            .references(ApiKeys.id)

    override val primaryKey =
        PrimaryKey(id)
}