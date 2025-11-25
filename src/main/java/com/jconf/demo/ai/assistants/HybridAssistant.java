package com.jconf.demo.ai.assistants;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

@AiService
public interface HybridAssistant {

    @SystemMessage("""
        Eres un asistente del banco.
        Respondes SOLO usando la información de los documentos recuperados.
        Si no sabes, di que no sabes.
    """)
    String chat(@UserMessage String question);
}
