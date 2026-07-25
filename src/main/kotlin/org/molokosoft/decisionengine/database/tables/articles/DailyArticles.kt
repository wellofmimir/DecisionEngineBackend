package org.molokosoft.decisionengine.database.tables.articles

import org.jetbrains.exposed.sql.Table

object DailyArticles : Table("DailyArticles") {

    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val topic = varchar("topic", 255)
    val readingTimeMinutes = integer("readingTimeMinutes")
    val summary = varchar("summary", 500)
    val content = text("content")
    val takeAwayPoints = text("takeAwayPoints") //saved as JSON ["test", "point", "text"]

    override val primaryKey = PrimaryKey(id)
}