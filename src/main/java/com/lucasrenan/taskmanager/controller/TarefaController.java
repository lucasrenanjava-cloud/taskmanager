package com.lucasrenan.taskmanager.controller;
import com.lucasrenan.taskmanager.dto.request.AtualizarStatusRequest;
import com.lucasrenan.taskmanager.dto.request.TarefaRequest;
import com.lucasrenan.taskmanager.dto.response.TarefaResponse;
import com.lucasrenan.taskmanager.model.entity.User;
import com.lucasrenan.taskmanager.model.enums.Prioridade;
import com.lucasrenan.taskmanager.model.enums.StatusTarefa;
import com.lucasrenan.taskmanager.service.TarefaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @PostMapping
    public ResponseEntity<TarefaResponse> criar(@RequestBody @Valid TarefaRequest request,
                                                @AuthenticationPrincipal User usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tarefaService.criar(request, usuario));
    }

    @GetMapping
    public ResponseEntity<Page<TarefaResponse>> listar(@AuthenticationPrincipal User usuario,
                                                       Pageable pageable) {
        return ResponseEntity.ok(tarefaService.listar(usuario, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarefaResponse> buscarPorId(@PathVariable Long id,
                                                      @AuthenticationPrincipal User usuario) {
        return ResponseEntity.ok(tarefaService.buscarPorId(id, usuario));
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<TarefaResponse>> filtrar(@AuthenticationPrincipal User usuario,
                                                        @RequestParam(required = false) StatusTarefa status,
                                                        @RequestParam(required = false) Prioridade prioridade) {
        if (status != null) {
            return ResponseEntity.ok(tarefaService.filtrarPorStatus(usuario, status));
        }
        if (prioridade != null) {
            return ResponseEntity.ok(tarefaService.filtrarPorPrioridade(usuario, prioridade));
        }
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/atrasadas")
    public ResponseEntity<List<TarefaResponse>> listarAtrasadas(@AuthenticationPrincipal User usuario) {
        return ResponseEntity.ok(tarefaService.listarAtrasadas(usuario));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<TarefaResponse>> buscarPorTitulo(@AuthenticationPrincipal User usuario,
                                                                @RequestParam String titulo) {
        return ResponseEntity.ok(tarefaService.buscarPorTitulo(usuario, titulo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarefaResponse> atualizar(@PathVariable Long id,
                                                    @RequestBody @Valid TarefaRequest request,
                                                    @AuthenticationPrincipal User usuario) {
        return ResponseEntity.ok(tarefaService.atualizar(id, request, usuario));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TarefaResponse> atualizarStatus(@PathVariable Long id,
                                                          @RequestBody @Valid AtualizarStatusRequest request,
                                                          @AuthenticationPrincipal User usuario) {
        return ResponseEntity.ok(tarefaService.atualizarStatus(id, request, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id,
                                        @AuthenticationPrincipal User usuario) {
        tarefaService.deletar(id, usuario);
        return ResponseEntity.noContent().build();
    }
}