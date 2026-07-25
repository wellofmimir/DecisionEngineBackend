package org.molokosoft.decisionengine.database.tables.users

import org.jetbrains.exposed.sql.Table

object Users : Table("Users") {

    val id = integer("id").autoIncrement()
    val eMail = varchar("eMail", 255).uniqueIndex()
    val trialStarted = long("trialStarted")
    val trialEnds = long("trialEnds")
    val reminderSent = bool("reminderSent")

    override val primaryKey = PrimaryKey(id)
}