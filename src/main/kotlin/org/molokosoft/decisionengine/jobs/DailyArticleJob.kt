package org.molokosoft.decisionengine.jobs

import org.molokosoft.decisionengine.database.tables.articles.DailyArticleEntry
import org.molokosoft.decisionengine.repositories.articles.ArticlesRepository
import org.molokosoft.decisionengine.services.articles.ArticlesService

class DailyArticleJob(
    private val articlesRepository: ArticlesRepository,
    private val articlesService: ArticlesService
) : Job {
    override suspend fun execute() {
        val dailyArticle = articlesService.dailyArticle(
            articlesRepository.topics.random()
        )

        if (dailyArticle == null)
            return

        val dailyArticleEntry = DailyArticleEntry(
            title =  dailyArticle.title,
            topic = dailyArticle.topic,
            readingTimeMinutes = dailyArticle.readingTimeMinutes,
            summary = dailyArticle.summary,
            content = dailyArticle.content,
            takeAwayPoints = dailyArticle.takeAwayPoints
        )

        articlesRepository.insertDailyArticle(dailyArticleEntry)
    }
}