package org.molokosoft.decisionengine.services

import org.molokosoft.decisionengine.ai.v1.clients.OpenAiClient
import org.molokosoft.decisionengine.network.HttpClient
import org.molokosoft.decisionengine.ai.v1.prompts.OpenAiPromptBuilder
import org.molokosoft.decisionengine.repositories.articles.ArticlesRepository
import org.molokosoft.decisionengine.services.criteria.CriteriaService
import org.molokosoft.decisionengine.services.decision.DecisionService
import org.molokosoft.decisionengine.services.email.EMailService
import org.molokosoft.decisionengine.repositories.users.UserRepository
import org.molokosoft.decisionengine.services.articles.ArticlesService
import org.molokosoft.decisionengine.services.billing.GooglePlayService
import org.molokosoft.decisionengine.services.email.clients.EMailClient
import org.molokosoft.decisionengine.services.fileservices.FeedbackFileService
import org.molokosoft.decisionengine.services.fileservices.QuoteFileService
import org.molokosoft.decisionengine.services.security.SecurityService

class Services {
    val aiClient = OpenAiClient(HttpClient.client)
    val eMailClient = EMailClient(HttpClient.client)
    val promptBuilder = OpenAiPromptBuilder()

    val articlesRepository = ArticlesRepository()
    val userRepository = UserRepository()

    val decisionService = DecisionService(aiClient, promptBuilder)
    val criteriaService = CriteriaService(aiClient, promptBuilder)
    val securityService = SecurityService(aiClient, promptBuilder)
    val articlesService = ArticlesService(aiClient, promptBuilder)
    val eMailService = EMailService(userRepository, eMailClient)

    val feedbackFileService = FeedbackFileService()
    val quoteFileService = QuoteFileService()

    val googlePlayService = GooglePlayService(HttpClient.client)
}