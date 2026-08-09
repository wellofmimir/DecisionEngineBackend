package org.molokosoft.decisionengine.api.v1.quote

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.molokosoft.decisionengine.api.v1.articles.model.responses.DailyArticleResponse
import org.molokosoft.decisionengine.api.v1.articles.toDTO
import org.molokosoft.decisionengine.api.v1.model.ApiError
import org.molokosoft.decisionengine.api.v1.model.ApiResponse
import org.molokosoft.decisionengine.api.v1.quote.model.response.QuoteResponse
import org.molokosoft.decisionengine.authentication.AuthenticationNames
import org.molokosoft.decisionengine.services.fileservices.QuoteFileService

fun Route.quoteRoutes(
    quoteFileService: QuoteFileService
) {
    route("/quotes") {
        authenticate(AuthenticationNames.API_KEY) {
            rateLimit(RateLimitName("quotes")) {
                get("/daily") {
                    val quoteFile = quoteFileService.getRandomQuoteFile()
                        .getOrElse {
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
                            data = QuoteResponse(
                                quote = quoteFile
                            )
                        )
                    )
                }
            }
        }
    }
}