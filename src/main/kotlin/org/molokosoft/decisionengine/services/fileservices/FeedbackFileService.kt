package org.molokosoft.decisionengine.services.fileservices

import java.util.UUID
import java.io.File

class FeedbackFileService(
    private val feedbackFilesPath: String = "C:/DecisionEngine/feedback/"
) {

    fun saveFeedback(
        feedback: String
    ) {
        val directory = File(feedbackFilesPath)

        require(directory.exists() || directory.mkdirs()) {
            "Directory could not be created: ${directory.absolutePath}"
        }

        val id = UUID
            .randomUUID()
            .toString()
            .replace("-", "")
            .take(10)

        val feedbackFile = File(directory, "$id.txt")
        feedbackFile.writeText(feedback)
    }
}