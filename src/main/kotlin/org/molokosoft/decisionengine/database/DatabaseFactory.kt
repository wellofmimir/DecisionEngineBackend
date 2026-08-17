package org.molokosoft.decisionengine.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.molokosoft.decisionengine.database.tables.apikeys.ApiKeys
import org.molokosoft.decisionengine.database.tables.articles.DailyArticles
import org.molokosoft.decisionengine.database.tables.purchases.Purchases
import org.molokosoft.decisionengine.database.tables.users.Users

object DatabaseFactory {
    fun init() {
        val url = System.getenv("DECISIONENGINE_DATABASE_URL")
            ?: error("DECISIONENGINE_DATABASE_URL is not set")

        val driver = System.getenv("DECISIONENGINE_DATABASE_DRIVER")
            ?: error("DECISIONENGINE_DATABASE_DRIVER is not set")

        Database.connect(
            url = url,
            driver = driver
        )

        transaction {
            SchemaUtils.create(Users)
            SchemaUtils.create(ApiKeys)
            SchemaUtils.create(DailyArticles)
            SchemaUtils.create(Purchases)
        }
    }
}