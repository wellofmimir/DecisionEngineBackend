package org.molokosoft.decisionengine.services.fileservices

import org.molokosoft.decisionengine.api.v1.quote.model.dto.Quote
import java.io.File
import kotlinx.serialization.json.Json


class QuoteFileService(
    private val quoteFilesPath: String = "C:/DecisionEngine/quotes/"
) {
    fun getRandomQuoteFile(): Result<Quote> = runCatching {
        val directory = File(quoteFilesPath)

        require(directory.exists() || directory.mkdirs()) {
            "Directory could not be created: ${directory.absolutePath}"
        }

        val jsonFile = directory.listFiles() { file ->
            file.isFile && file.extension.equals("json", ignoreCase = true)
        }?.randomOrNull()
            ?: throw NoSuchElementException("No Quote found.")

        val json: Json = Json
        json.decodeFromString<Quote>(jsonFile.readText())
    }
}