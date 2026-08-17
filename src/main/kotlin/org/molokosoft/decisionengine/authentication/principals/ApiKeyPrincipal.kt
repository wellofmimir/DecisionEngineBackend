package org.molokosoft.decisionengine.authentication.principals

data class ApiKeyPrincipal(
    val apiKeyID: Int,
    val apiKeyHash: String
)
