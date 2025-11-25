package com.jconf.demo.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.azure.AzureOpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

// modelo de open ai utilizando azure
@Profile("azure")
@Configuration
public class AzureOpenAiModelsConfig {

    @Bean
    public ChatLanguageModel azureChatModel(
            @Value("${AZURE_OPENAI_ENDPOINT}") String endpoint,
            @Value("${AZURE_OPENAI_API_KEY}") String apiKey,
            @Value("${AZURE_OPENAI_DEPLOYMENT_NAME}") String deploymentName) {

        return AzureOpenAiChatModel.builder()
                .apiKey(apiKey)
                .endpoint(endpoint)
                .deploymentName(deploymentName)
                .temperature(0.1)
                .build();
    }

    @Bean
    public EmbeddingModel azureEmbeddingModel(
            @Value("${AZURE_OPENAI_ENDPOINT}") String endpoint,
            @Value("${AZURE_OPENAI_API_KEY}") String apiKey,
            @Value("${AZURE_OPENAI_EMBEDDING_DEPLOYMENT}") String deploymentName) {

        return AzureOpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .endpoint(endpoint)
                .deploymentName(deploymentName)
                .build();
    }
}
