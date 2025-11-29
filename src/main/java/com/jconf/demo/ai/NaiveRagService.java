package com.jconf.demo.ai;

import com.jconf.demo.ai.assistants.BankAssistant;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Service;

/**
 * Servicio para el Acto 1.
 * En este acto solo delega al BankAssistant.
 */
@Service
public class NaiveRagService {

    private final BankAssistant bankAssistant;
    private final ObservationRegistry observationRegistry;

    public NaiveRagService(BankAssistant bankAssistant,
                           ObservationRegistry observationRegistry) {
        this.bankAssistant = bankAssistant;
        this.observationRegistry = observationRegistry;
    }

    public String ask(String question) {

        Observation obs = Observation
                .start("gen_ai.chat", observationRegistry)
                .lowCardinalityKeyValue("gen_ai.operation.name", "chat")
                .highCardinalityKeyValue("gen_ai.content.prompt", question);

        try (Observation.Scope scope = obs.openScope()) {
            String answer = bankAssistant.chat(question);
            obs.highCardinalityKeyValue("gen_ai.content.completion", answer);

            return answer;
        } finally {
            obs.stop();
        }
    }
}