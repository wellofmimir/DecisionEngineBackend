package org.molokosoft.decisionengine.database.tables.apikeys

data class ApiKey(
    val id: Int,
    val apiKeyHash: String,
    val remainingUsages: Int,
    val isActive: Boolean
)