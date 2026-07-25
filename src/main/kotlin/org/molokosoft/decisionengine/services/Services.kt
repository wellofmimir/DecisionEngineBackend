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
import org.molokosoft.decisionengine.services.email.clients.EMailClient
import org.molokosoft.decisionengine.services.fileservices.FeedbackFileService

class Services {
    val aiClient = OpenAiClient(HttpClient.client)
    val promptBuilder = OpenAiPromptBuilder()
    val decisionService = DecisionService(aiClient, promptBuilder)
    val criteriaService = CriteriaService(aiClient, promptBuilder)
    val articlesRepository = ArticlesRepository()
    val articlesService = ArticlesService(aiClient, promptBuilder)

    val userRepository = UserRepository()
    val eMailClient = EMailClient(HttpClient.client)
    val eMailService = EMailService(userRepository, eMailClient)

    val feedbackFileService = FeedbackFileService()
}