package org.molokosoft.decisionengine.database.tables.apikeys

data class ApiKey(
    val id: Int,
    val apiKeyHash: String,
    val subscriptionUsages: Int,
    val consumableUsages: Int,
    val isActive: Boolean
)