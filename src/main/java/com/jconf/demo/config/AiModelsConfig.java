package com.jconf.demo.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Modelos base para el Acto 1.
 * Si luego quieres usar Azure OpenAI, solo cambias aquí.
 */
@Configuration
public class AiModelsConfig {

    private static final String OLLAMA_BASE_URL = "http://localhost:11434";
    private static final String OLLAMA_CHAT_MODEL = "llama3.1";          // modelo de chat
    private static final String OLLAMA_EMBED_MODEL = "nomic-embed-text"; // modelo de embeddings

    @Bean
    public ChatLanguageModel ollamaChatModel() {
        return OllamaChatModel.builder()
                .baseUrl(OLLAMA_BASE_URL)
                .modelName(OLLAMA_CHAT_MODEL)
                .timeout(Duration.ofSeconds(60))
                .build();
    }

    @Bean
    public EmbeddingModel ollamaEmbeddingModel() {
        return OllamaEmbeddingModel.builder()
                .baseUrl(OLLAMA_BASE_URL)
                .modelName(OLLAMA_EMBED_MODEL)
                .timeout(Duration.ofSeconds(60))
                .build();
    }
}
