package org.molokosoft.decisionengine.extensions

import io.ktor.server.request.receive
import io.ktor.server.application.ApplicationCall
import  org.molokosoft.decisionengine.api.v1.model.Validatable

suspend inline fun <reified T> ApplicationCall.receiveValidated(): T where T : Validatable {

    val dto = receive<T>()
    dto.validate()
    return dto
}