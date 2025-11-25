package com.jconf.demo.ai;

import com.jconf.demo.ai.assistants.BankAssistant;
import org.springframework.stereotype.Service;

/**
 * Servicio para el Acto 1.
 * En este acto solo delega al BankAssistant.
 */
@Service
public class NaiveRagService {

    private final BankAssistant bankAssistant;

    public NaiveRagService(BankAssistant bankAssistant) {
        this.bankAssistant = bankAssistant;
    }

    public String ask(String question) {
        return bankAssistant.chat(question);
    }
}
