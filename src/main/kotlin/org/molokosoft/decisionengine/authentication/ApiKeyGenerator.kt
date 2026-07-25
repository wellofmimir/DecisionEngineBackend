package org.molokosoft.decisionengine.authentication

import java.security.SecureRandom
import java.util.Base64

object ApiKeyGenerator {

    private val secureRandom = SecureRandom()

    fun generate(): String {
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)

        return "DE_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}