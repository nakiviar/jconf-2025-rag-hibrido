package com.jconf.demo.config;

import com.jconf.demo.ai.assistants.BankAssistant;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Acto 1:
 * Construye un BankAssistant que:
 *  - usa embeddings + vector store en memoria
 *  - hace retrieval con EmbeddingStoreContentRetriever
 */
@Configuration
public class Act1NaiveRagConfig {

    @Bean
    BankAssistant bankAssistant(
            @Qualifier("ollamaChatModel") ChatLanguageModel chatModel,
            ContentRetriever observedContentRetriever) {

        return AiServices.builder(BankAssistant.class)
                .chatLanguageModel(chatModel)
                .contentRetriever(observedContentRetriever)
                .systemMessageProvider(__ -> """
                        Eres un asistente virtual de un banco.
                        Respondes SOLO usando la información de los documentos internos.
                        Si la respuesta no está en los documentos, di que no lo sabes.
                        """)
                .build();
    }
}
