package org.molokosoft.decisionengine.jobs

interface Job {
    suspend fun execute()
}