package org.molokosoft.decisionengine.database.tables.users

data class User(
    val id: Int,
    val eMail: String,
    val trialStarted: Long,
    val trialEnds: Long,
    val reminderSent: Boolean
)