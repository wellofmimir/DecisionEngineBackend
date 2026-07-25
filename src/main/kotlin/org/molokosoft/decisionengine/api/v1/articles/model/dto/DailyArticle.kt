package org.molokosoft.decisionengine.api.v1.articles.model.dto

import kotlinx.serialization.Serializable
import org.molokosoft.decisionengine.api.v1.model.Validatable

@Serializable
data class DailyArticle(
    val title: String,
    val topic: String,
    val readingTimeMinutes: Int,
    val summary: String,
    val content: String,
    val takeAwayPoints: List<String>
) : Validatable {
    override fun validate() {
    }
}