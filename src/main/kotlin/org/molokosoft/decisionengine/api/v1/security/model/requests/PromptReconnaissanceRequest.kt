package org.molokosoft.decisionengine.api.v1.security.model.requests

import kotlinx.serialization.Serializable
import org.molokosoft.decisionengine.api.v1.model.Validatable

@Serializable
data class PromptReconnaissanceRequest(
    val prompt: String
) : Validatable {
    override fun validate() {
    }
}