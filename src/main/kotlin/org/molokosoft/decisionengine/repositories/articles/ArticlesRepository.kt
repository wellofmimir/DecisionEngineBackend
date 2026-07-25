package org.molokosoft.decisionengine.repositories.articles

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.molokosoft.decisionengine.api.v1.articles.model.dto.DailyArticle
import org.molokosoft.decisionengine.database.tables.articles.DailyArticleEntry
import org.molokosoft.decisionengine.database.tables.articles.DailyArticles

class ArticlesRepository {

    fun insertDailyArticle(dailyArticle: DailyArticleEntry) = transaction {
        val takeAwayPointsJson = Json.encodeToString(dailyArticle.takeAwayPoints)

        DailyArticles.insert {
            it[DailyArticles.title] = dailyArticle.title
            it[DailyArticles.topic] = dailyArticle.topic
            it[DailyArticles.summary] = dailyArticle.summary
            it[DailyArticles.content] = dailyArticle.content
            it[DailyArticles.readingTimeMinutes] = dailyArticle.readingTimeMinutes
            it[DailyArticles.takeAwayPoints] = takeAwayPointsJson
        }
    }

    fun dailyArticle(): DailyArticleEntry? = transaction {
        DailyArticles
            .selectAll()
            .orderBy(DailyArticles.id, SortOrder.DESC)
            .limit(1)
            .map {
                DailyArticleEntry(
                    title = it[DailyArticles.title],
                    topic = it[DailyArticles.topic],
                    readingTimeMinutes = it[DailyArticles.readingTimeMinutes],
                    summary = it[DailyArticles.summary],
                    content = it[DailyArticles.content],
                    takeAwayPoints = Json.decodeFromString(it[DailyArticles.takeAwayPoints])
                )
            }
            .singleOrNull()
    }
}