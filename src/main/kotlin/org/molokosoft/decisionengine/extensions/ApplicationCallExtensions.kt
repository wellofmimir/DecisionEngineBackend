package org.molokosoft.decisionengine.extensions

import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.application.ApplicationCall
import  org.molokosoft.decisionengine.api.v1.model.Validatable
import org.molokosoft.decisionengine.exceptions.BadRequestException

suspend inline fun <reified T> ApplicationCall.receiveValidated(): T where T : Validatable {

    val dto = receive<T>()
    dto.validate()
    return dto
}

fun ApplicationCall.requireInstallationId(): String {
    return request.headers["X-Installation-ID"]
        ?.takeIf {
            it.isNotBlank()
        }
        ?: throw BadRequestException("Missing X-Installation-ID.")
}