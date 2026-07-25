package org.molokosoft.decisionengine.services.authentication

import org.molokosoft.decisionengine.authentication.ApiKeyGenerator
import org.molokosoft.decisionengine.authentication.ApiKeyHasher
import org.molokosoft.decisionengine.repositories.users.UserRepository

class AuthenticationService(
    private val userRepository: UserRepository
) {
    fun apiKey(): String {
        return ApiKeyGenerator.generate()
    }

    fun saveApiKey(apiKey: String) {
        val apiKeyHash = ApiKeyHasher.sha256(apiKey)
        userRepository.insertApiKey(apiKeyHash)
    }
}