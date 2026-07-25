package org.molokosoft.decisionengine.api.v1.feedback

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.molokosoft.decisionengine.api.v1.feedback.model.requests.FeedbackRequest

import org.molokosoft.decisionengine.api.v1.model.ApiResponse
import org.molokosoft.decisionengine.extensions.receiveValidated
import org.molokosoft.decisionengine.services.fileservices.FeedbackFileService

fun Route.feedbackRoutes(
    feedbackFileService: FeedbackFileService
) {
    route("/feedback") {
        rateLimit(RateLimitName("feedback")) {
            post("/send") {

                val request = call.receiveValidated<FeedbackRequest>()
                feedbackFileService.saveFeedback(request.feedback)

                call.respond(
                    status = HttpStatusCode.OK,
                    message = ApiResponse(
                        success = true,
                        data = null
                    )
                )
            }
        }
    }
}