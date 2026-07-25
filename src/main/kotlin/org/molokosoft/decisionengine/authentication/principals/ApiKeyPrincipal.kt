package org.molokosoft.decisionengine.authentication.principals

import org.molokosoft.decisionengine.database.tables.apikeys.ApiKey

data class ApiKeyPrincipal(
    val apiKeyID: Int
)
