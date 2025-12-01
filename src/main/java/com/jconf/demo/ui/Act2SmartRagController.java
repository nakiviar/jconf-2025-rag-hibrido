package com.jconf.demo.ui;

import com.jconf.demo.ai.SmartRagService;
import com.jconf.demo.dto.AskRequest;
import com.jconf.demo.dto.AskResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/act2")
public class Act2SmartRagController {

    private final SmartRagService smartRagService;

    public Act2SmartRagController(SmartRagService smartRagService) {
        this.smartRagService = smartRagService;
    }

    @PostMapping("/ask")
    public AskResponse ask(@RequestBody AskRequest request) {
        String answer = smartRagService.askSmart(request.question());
        return new AskResponse(answer);
    }
}