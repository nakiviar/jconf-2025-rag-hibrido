package com.jconf.demo.ai.assistants;


import com.jconf.demo.dto.QueryAnalysis;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface QueryAnalyzer {

    @SystemMessage("""
        Eres un asistente que analiza consultas de clientes de un banco.
        Tareas:
        1) Normaliza la pregunta en español técnico.
        2) Clasifica la intención en: BANKING_QUESTION | SMALL_TALK | OUT_OF_SCOPE.

        Responde con JSON **estricto** y nada más (sin texto adicional, sin Markdown):
        {"normalized":"<texto>","intent":"<BANKING_QUESTION|SMALL_TALK|OUT_OF_SCOPE>"}
        """)
    @UserMessage("Consulta del cliente: {{input}}")
    QueryAnalysis analyze(@V("input") String input);
}