package org.molokosoft.decisionengine.api.v1.security

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.response.*
import io.ktor.server.routing.*

import org.molokosoft.decisionengine.api.v1.model.ApiError
import org.molokosoft.decisionengine.api.v1.model.ApiResponse
import org.molokosoft.decisionengine.api.v1.security.model.requests.PromptReconnaissanceRequest
import org.molokosoft.decisionengine.api.v1.security.model.responses.PromptReconnaissanceResponse
import org.molokosoft.decisionengine.extensions.receiveValidated
import org.molokosoft.decisionengine.services.security.SecurityService

fun Route.securityRoutes(
    securityService: SecurityService
) {
   route("/security") {
       rateLimit(RateLimitName("promptReconnaissance")) {
            post("/promptReconnaissance") {

                val request =
                    call.receiveValidated<PromptReconnaissanceRequest>()

                val result =
                    securityService.promptReconnaissance(request)

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
                        data = PromptReconnaissanceResponse(
                            result = result
                        )
                    )
                )
            }
       }
   }
}