package org.molokosoft.decisionengine.authentication

import java.security.MessageDigest

object ApiKeyHasher {

    fun sha256(apiKey: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(apiKey.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}