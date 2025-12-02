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
import org.springframework.context.annotation.*;

@Configuration
public class Act3TextConfig {

    @Value("${app.azure-search.endpoint}") String endpoint;
    @Value("${app.azure-search.api-key}") String apiKey;
    @Value("${app.azure-search.index}") String index;
    @Value("${app.azure-search.text-field}") String textField;
    @Value("${app.azure-search.k:5}") int k;

    @Bean
    public ContentRetriever azureTextRetriever(ObservationRegistry reg) {

        var base = new AzureSearchTextRetriever(endpoint, apiKey, index, textField, k);
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
                    Responde únicamente usando el contexto recuperado.
                    Si el contexto no es suficiente, adviértelo.
                """)
                .build();
    }
}