package com.jconf.demo.ui;

import com.jconf.demo.ai.assistants.BankAssistant;
import com.jconf.demo.dto.AskRequest;
import com.jconf.demo.dto.AskResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/act3")
public class Act3AzureController {

    private final BankAssistant assistant;

    public Act3AzureController(
            @Qualifier("bankAssistantAct3") BankAssistant assistant
    ) {
        this.assistant = assistant;
    }

    @PostMapping("/ask")
    public AskResponse ask(@RequestBody AskRequest request) {
        String answer = assistant.chat(request.question());
        return new AskResponse(answer);
    }
}