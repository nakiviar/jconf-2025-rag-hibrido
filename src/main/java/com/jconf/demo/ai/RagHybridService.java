package com.jconf.demo.ai;

import com.jconf.demo.ai.assistants.HybridAssistant;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
// no se usa
@Profile("azure")
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