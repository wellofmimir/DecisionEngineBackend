package org.molokosoft.decisionengine.api.v1.articles

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import io.ktor.server.routing.get
import io.ktor.server.response.*
import kotlinx.serialization.json.Json
import org.molokosoft.decisionengine.api.v1.articles.model.dto.DailyArticle
import org.molokosoft.decisionengine.api.v1.articles.model.responses.DailyArticleResponse
import org.molokosoft.decisionengine.api.v1.model.ApiError
import org.molokosoft.decisionengine.api.v1.model.ApiResponse
import org.molokosoft.decisionengine.authentication.AuthenticationNames
import org.molokosoft.decisionengine.database.tables.articles.DailyArticleEntry
import org.molokosoft.decisionengine.repositories.articles.ArticlesRepository

fun DailyArticleEntry.toDTO(): DailyArticle {
    return DailyArticle(
        title = this.title,
        topic = this.topic,
        readingTimeMinutes = this.readingTimeMinutes,
        summary = this.summary,
        content = this.content,
        takeAwayPoints = this.takeAwayPoints
    )
}

fun Route.articleRoutes(
    articlesRepository: ArticlesRepository
) {
    route("/articles") {
        authenticate(AuthenticationNames.API_KEY) {

        }

        rateLimit(RateLimitName("articles")) {
            get("/daily") {
                val dailyArticle = articlesRepository.dailyArticle()

                if (dailyArticle == null) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = ApiResponse(
                            success = false,
                            data = ApiError(
                                code = HttpStatusCode.InternalServerError.toString(),
                                message = "Internal server error."
                            )
                        )
                    )

                    return@get
                }

                call.respond(
                    status = HttpStatusCode.OK,
                    message = ApiResponse(
                        success = true,
                        data = DailyArticleResponse(
                            dailyArticle = dailyArticle.toDTO()
                        )
                    )
                )
            }
        }
    }
}