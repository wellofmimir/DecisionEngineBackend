package org.molokosoft.decisionengine.api.v1.email

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.response.*
import io.ktor.server.routing.*

import org.molokosoft.decisionengine.api.v1.model.ApiResponse
import org.molokosoft.decisionengine.extensions.receiveValidated
import org.molokosoft.decisionengine.api.v1.email.model.requests.EMailRequest
import org.molokosoft.decisionengine.model.EMail
import org.molokosoft.decisionengine.services.email.EMailService

fun Route.emailRoutes(
    eMailService: EMailService
) {
    rateLimit(RateLimitName("email")) {
        post("/email") {
            val request = call.receiveValidated<EMailRequest>()
            eMailService.save(EMail(request.eMail))

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