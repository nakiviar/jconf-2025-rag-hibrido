package com.jconf.demo.ai.assistants;


import dev.langchain4j.service.UserMessage;

/**
 * Asistente bancario básico para el Acto 1.
 * La implementación real la genera LangChain4j con AiServices.builder(...).
 */
public interface BankAssistant {

    String chat(@UserMessage String question);
}
