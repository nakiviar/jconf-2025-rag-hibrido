package com.jconf.demo.ai;

import com.jconf.demo.ai.assistants.BankAssistant;
import com.jconf.demo.ai.assistants.QueryAnalyzer;
import com.jconf.demo.dto.QueryAnalysis;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Service;

/**
 * Servicio para el Acto 2.
 * En este acto se maneja mejoras en el pre-retrieval
 */
@Service
public class SmartRagService {

    private final QueryAnalyzer queryAnalyzer;
    private final BankAssistant bankAssistant;   // el mismo del Acto 1
    private final ObservationRegistry observationRegistry;

    public SmartRagService(QueryAnalyzer queryAnalyzer,
                           BankAssistant bankAssistant,
                           ObservationRegistry observationRegistry) {
        this.queryAnalyzer = queryAnalyzer;
        this.bankAssistant = bankAssistant;
        this.observationRegistry = observationRegistry;
    }


    public String askSmart(String question) {

        Observation rewriteObs = Observation
                .start("rag.query.rewrite", observationRegistry)
                .highCardinalityKeyValue("rag.original.question", question);

        String normalized;
        String intent;

        try (Observation.Scope scope = rewriteObs.openScope()) {

            QueryAnalysis analysis = queryAnalyzer.analyze(question);
            normalized = analysis.normalized();
            intent = analysis.intent();

            rewriteObs.highCardinalityKeyValue("rag.normalized.question", normalized);
            rewriteObs.highCardinalityKeyValue("rag.intent", intent);
//
//        } catch (Exception e) {
//            // fallback razonable para no reventar la demo
//            normalized = question;
//            intent = "BANKING_QUESTION";
//            rewriteObs.highCardinalityKeyValue("rag.error", e.getClass().getSimpleName());
        } finally {
            rewriteObs.stop();
        }

        // Routing según intent
        if ("SMALL_TALK".equalsIgnoreCase(intent)) {
            return "Puedo ayudarte con productos y políticas del banco. ¿Qué deseas consultar?";
        }
        if ("OUT_OF_SCOPE".equalsIgnoreCase(intent)) {
            return "Lo que me preguntas no está cubierto por la información del banco.";
        }

        // Intent BANKING_QUESTION → ejecuta el RAG de Acto 1 pero con la pregunta normalizada
        Observation chatObs = Observation
                .start("gen_ai.chat.act2", observationRegistry)
                .highCardinalityKeyValue("gen_ai.content.prompt.original", question)
                .highCardinalityKeyValue("gen_ai.content.prompt.normalized", normalized);

        try (Observation.Scope scope = chatObs.openScope()) {
            String answer = bankAssistant.chat(normalized);
            chatObs.highCardinalityKeyValue("gen_ai.content.completion", answer);
            return answer;
        } finally {
            chatObs.stop();
        }
    }

}
