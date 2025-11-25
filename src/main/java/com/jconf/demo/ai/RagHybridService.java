package com.jconf.demo.ai;

import com.jconf.demo.ai.assistants.HybridAssistant;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagHybridService {

    private final HybridAssistant assistant;

    public RagHybridService(HybridAssistant assistant) {
        this.assistant = assistant;
    }

    public String chat(String question) {
        return assistant.chat(question);
    }
}