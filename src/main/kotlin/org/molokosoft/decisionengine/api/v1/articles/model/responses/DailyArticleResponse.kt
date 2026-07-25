package org.molokosoft.decisionengine.api.v1.articles.model.responses

import kotlinx.serialization.Serializable
import org.molokosoft.decisionengine.api.v1.articles.model.dto.DailyArticle

@Serializable
data class DailyArticleResponse(
    val dailyArticle: DailyArticle
)