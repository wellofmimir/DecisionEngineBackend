package org.molokosoft.decisionengine.database.tables.articles

data class DailyArticleEntry(
    val title: String,
    val topic: String,
    val readingTimeMinutes: Int,
    val summary: String,
    val content: String,
    val takeAwayPoints: List<String>
)