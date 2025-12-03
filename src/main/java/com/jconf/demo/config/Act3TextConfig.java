package com.jconf.demo.config;

import com.jconf.demo.ai.assistants.BankAssistant;
import com.jconf.demo.ai.retrievers.AzureSearchTextRetriever;
import com.jconf.demo.ai.retrievers.ObservedContentRetriever;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("act3")
public class Act3TextConfig {

    @Value("${app.azure-search.endpoint}") String endpoint;
    @Value("${app.azure-search.api-key}") String apiKey;
    @Value("${app.azure-search.index}") String index;
    @Value("${app.azure-search.text-field}") String textField;
    @Value("${app.azure-search.k:5}") int k;
    @Value("${app.azure-search.mode:text}") String mode;
    @Value("${app.azure-search.semantic-config:}") String semanticConfig;

    @Bean("azureTextRetriever")
    public ContentRetriever azureTextRetriever(ObservationRegistry reg) {

        AzureSearchTextRetriever.Mode m =
                AzureSearchTextRetriever.Mode.valueOf(mode.trim().toUpperCase());

        var base = new AzureSearchTextRetriever(
                endpoint,
                apiKey,
                index,
                textField,
                k,
                m,
                semanticConfig
        );

        // Envuelto con observabilidad Micrometer (span "rag.content.retriever")
        return new ObservedContentRetriever(base, reg, "azure-text-sdk");
    }

    @Bean(name = "bankAssistantAct3")
    public BankAssistant bankAssistantAct3(
            @Qualifier("ollamaChatModel") ChatLanguageModel chat,
            @Qualifier("azureTextRetriever") ContentRetriever retriever) {

        return AiServices.builder(BankAssistant.class)
                .chatLanguageModel(chat)
                .contentRetriever(retriever)
                .systemMessageProvider(__ -> """
                    Eres un asistente del banco.
                    Responde únicamente usando el contexto recuperado de Azure AI Search.
                    Si el contexto no es suficiente, adviértelo AL USUARIO de forma clara.
                """)
                .build();
    }
}
