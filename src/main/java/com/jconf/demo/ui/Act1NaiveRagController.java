package com.jconf.demo.ui;

import com.jconf.demo.ai.NaiveRagService;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoint para probar el Acto 1 (RAG ingenuo local).
 *
 * POST /api/act1/ask
 * {
 *   "question": "¿Cuál es la comisión de mantenimiento de la Cuenta Ahorro Plus?"
 * }
 */
@RestController
@RequestMapping("/api/act1")
public class Act1NaiveRagController {

    private record RagRequest(String question) {}
    private record RagResponse(String answer) {}

    private final NaiveRagService naiveRagService;

    public Act1NaiveRagController(NaiveRagService naiveRagService) {
        this.naiveRagService = naiveRagService;
    }

    @PostMapping("/ask")
    public RagResponse ask(@RequestBody RagRequest request) {
        String answer = naiveRagService.ask(request.question());
        return new RagResponse(answer);
    }
}
