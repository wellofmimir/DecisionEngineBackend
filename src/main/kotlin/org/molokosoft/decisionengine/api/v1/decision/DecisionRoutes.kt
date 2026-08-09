package org.molokosoft.decisionengine.api.v1.decision

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.response.*
import io.ktor.server.routing.*

import org.molokosoft.decisionengine.api.v1.decision.model.requests.DecisionAnalysisRequest
import org.molokosoft.decisionengine.api.v1.decision.model.requests.SafetyClassificationRequest
import org.molokosoft.decisionengine.api.v1.decision.model.responses.DecisionAnalysisResponse
import org.molokosoft.decisionengine.api.v1.decision.model.responses.SafetyClassificationResponse
import org.molokosoft.decisionengine.api.v1.model.ApiError
import org.molokosoft.decisionengine.api.v1.model.ApiResponse
import org.molokosoft.decisionengine.authentication.AuthenticationNames
import org.molokosoft.decisionengine.extensions.receiveValidated
import org.molokosoft.decisionengine.services.decision.DecisionService

fun Route.decisionRoutes(
    decisionService: DecisionService
) {
    route("/decision") {
        rateLimit(RateLimitName("decision")) {
            authenticate(AuthenticationNames.API_KEY) {
                post("/analyze") {

                    val request = call.receiveValidated<DecisionAnalysisRequest>()
                    val result = decisionService.analyze(request)

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
                            data = DecisionAnalysisResponse(
                                result = result
                            )
                        )
                    )
                }
            }

            post("/safetyClassification") {
                val request = call.receiveValidated<SafetyClassificationRequest>()
                val result = decisionService.safetyClassification(request)

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
                        data = SafetyClassificationResponse(
                            safetyClassification = result
                        )
                    )
                )
            }
        }
    }
}