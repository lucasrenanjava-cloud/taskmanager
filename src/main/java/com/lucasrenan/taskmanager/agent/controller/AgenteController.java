package com.lucasrenan.taskmanager.agent.controller;
import com.lucasrenan.taskmanager.agent.dto.request.ChatRequest;

import com.lucasrenan.taskmanager.agent.dto.response.MensagemChatResponse;
import com.lucasrenan.taskmanager.model.entity.User;
import com.lucasrenan.taskmanager.agent.service.AgenteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/agente")
public class AgenteController {

    private final AgenteService agenteService;

    public AgenteController(AgenteService agenteService) {
        this.agenteService = agenteService;
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody @Valid ChatRequest request,
                                       @AuthenticationPrincipal User usuario) {
        return ResponseEntity.ok(agenteService.chat(request.mensagem(), usuario));
    }

    @GetMapping("/historico")
    public ResponseEntity<List<MensagemChatResponse>> listarHistorico(
            @AuthenticationPrincipal User usuario) {
        return ResponseEntity.ok(agenteService.listarHistorico(usuario));
    }

    @DeleteMapping("/historico")
    public ResponseEntity<Void> limparHistorico(@AuthenticationPrincipal User usuario) {
        agenteService.limparHistorico(usuario);
        return ResponseEntity.noContent().build();
    }
}


