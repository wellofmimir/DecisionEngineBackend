package org.molokosoft.decisionengine.api.v1.criteria

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

import org.molokosoft.decisionengine.api.v1.criteria.model.requests.CriteriaSuggestionRequest
import org.molokosoft.decisionengine.api.v1.criteria.model.responses.CriteriaSuggestionResponse
import org.molokosoft.decisionengine.api.v1.model.ApiError
import org.molokosoft.decisionengine.api.v1.model.ApiResponse
import org.molokosoft.decisionengine.extensions.receiveValidated
import org.molokosoft.decisionengine.services.criteria.CriteriaService

fun Route.criteriaRoutes(
    criteriaService: CriteriaService
) {
    route("/criteria") {
        rateLimit(RateLimitName("criteria")) {
            post("/suggest") {

                val request = call.receiveValidated<CriteriaSuggestionRequest>()
                val result = criteriaService.suggest(request)

                if (result == null) {
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

                    return@post
                }

                call.respond(
                    status = HttpStatusCode.OK,
                    message = ApiResponse(
                        success = true,
                        data = CriteriaSuggestionResponse(
                            criteria = result
                        )
                    )
                )
            }
        }
    }
}