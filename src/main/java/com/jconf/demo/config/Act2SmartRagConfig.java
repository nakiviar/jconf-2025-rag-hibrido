package com.jconf.demo.config;

import com.jconf.demo.ai.assistants.QueryAnalyzer;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Act2SmartRagConfig {

    @Bean
    public QueryAnalyzer queryAnalyzer(
            @Qualifier("ollamaChatModel") ChatLanguageModel chatModel) {

        return AiServices.create(QueryAnalyzer.class, chatModel);
    }
}