package org.molokosoft.decisionengine.api.v1.health

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.molokosoft.decisionengine.api.v1.health.model.HealthResponse
import org.molokosoft.decisionengine.api.v1.model.ApiResponse

fun Route.getHealth() {
    rateLimit(RateLimitName("health")) {
        get("/health") {
            call.respond(
                status = HttpStatusCode.OK,
                message = ApiResponse(
                    success = true,
                    data = HealthResponse(
                        status = "Ok."
                    )
                )
            )
        }
    }
}