package org.molokosoft.decisionengine.scheduler

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.molokosoft.decisionengine.jobs.Job
import kotlin.time.Duration

class Scheduler {
    fun every(
        interval: Duration,
        job: Job
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                try {
                    job.execute()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                delay(interval)
            }
        }
    }
}