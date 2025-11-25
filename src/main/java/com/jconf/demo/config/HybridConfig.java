package com.jconf.demo.config;

import com.jconf.demo.ai.assistants.HybridAssistant;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("azure")
@Configuration
public class HybridConfig {

    @Bean
    HybridAssistant hybridAssistant(@Qualifier("azureChatModel")  ChatLanguageModel chatModel,
                                     ContentRetriever hybridRetriever) {

        return AiServices.builder(HybridAssistant.class)
                .chatLanguageModel(chatModel)
                .contentRetriever(hybridRetriever)
                .build();
    }
}
