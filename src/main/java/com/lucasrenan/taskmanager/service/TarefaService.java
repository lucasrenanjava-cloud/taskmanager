package com.lucasrenan.taskmanager.service;

import com.lucasrenan.taskmanager.dto.request.AtualizarStatusRequest;
import com.lucasrenan.taskmanager.dto.request.TarefaRequest;
import com.lucasrenan.taskmanager.dto.response.TarefaResponse;
import com.lucasrenan.taskmanager.exception.AcessoNegadoException;
import com.lucasrenan.taskmanager.exception.TarefaNaoEncontradaException;
import com.lucasrenan.taskmanager.model.entity.Tarefa;
import com.lucasrenan.taskmanager.model.entity.User;
import com.lucasrenan.taskmanager.model.enums.Prioridade;
import com.lucasrenan.taskmanager.model.enums.StatusTarefa;
import com.lucasrenan.taskmanager.repository.TarefaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TarefaService {

    private final TarefaRepository tarefaRepository;

    public TarefaService(TarefaRepository tarefaRepository) {
        this.tarefaRepository = tarefaRepository;
    }

    public TarefaResponse criar(TarefaRequest request, User usuario) {
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(request.titulo());
        tarefa.setDescricao(request.descricao());
        tarefa.setPrioridade(request.prioridade() != null ? request.prioridade() : Prioridade.MEDIA);
        tarefa.setStatus(StatusTarefa.PENDENTE);
        tarefa.setPrazo(request.prazo());
        tarefa.setUsuario(usuario);

        return TarefaResponse.fromEntity(tarefaRepository.save(tarefa));
    }

    public Page<TarefaResponse> listar(User usuario, Pageable pageable) {
        return tarefaRepository.findByUsuarioId(usuario.getId(), pageable)
                .map(TarefaResponse::fromEntity);
    }

    public TarefaResponse buscarPorId(Long id, User usuario) {
        Tarefa tarefa = buscarTarefaDoUsuario(id, usuario);
        return TarefaResponse.fromEntity(tarefa);
    }

    public List<TarefaResponse> filtrarPorStatus(User usuario, StatusTarefa status) {
        return tarefaRepository.findByUsuarioIdAndStatus(usuario.getId(), status)
                .stream()
                .map(TarefaResponse::fromEntity)
                .toList();
    }

    public List<TarefaResponse> filtrarPorPrioridade(User usuario, Prioridade prioridade) {
        return tarefaRepository.findByUsuarioIdAndPrioridade(usuario.getId(), prioridade)
                .stream()
                .map(TarefaResponse::fromEntity)
                .toList();
    }

    public List<TarefaResponse> listarAtrasadas(User usuario) {
        return tarefaRepository.findByUsuarioIdAndPrazoBeforeAndStatusNot(
                        usuario.getId(), LocalDate.now(), StatusTarefa.CONCLUIDA)
                .stream()
                .map(TarefaResponse::fromEntity)
                .toList();
    }

    public List<TarefaResponse> buscarPorTitulo(User usuario, String titulo) {
        return tarefaRepository.findByUsuarioIdAndTituloContainingIgnoreCase(usuario.getId(), titulo)
                .stream()
                .map(TarefaResponse::fromEntity)
                .toList();
    }

    public TarefaResponse atualizar(Long id, TarefaRequest request, User usuario) {
        Tarefa tarefa = buscarTarefaDoUsuario(id, usuario);
        tarefa.setTitulo(request.titulo());
        tarefa.setDescricao(request.descricao());
        tarefa.setPrioridade(request.prioridade() != null ? request.prioridade() : tarefa.getPrioridade());
        tarefa.setPrazo(request.prazo());

        return TarefaResponse.fromEntity(tarefaRepository.save(tarefa));
    }

    public TarefaResponse atualizarStatus(Long id, AtualizarStatusRequest request, User usuario) {
        Tarefa tarefa = buscarTarefaDoUsuario(id, usuario);
        tarefa.setStatus(request.status());
        return TarefaResponse.fromEntity(tarefaRepository.save(tarefa));
    }

    public void deletar(Long id, User usuario) {
        Tarefa tarefa = buscarTarefaDoUsuario(id, usuario);
        tarefaRepository.delete(tarefa);
    }

    private Tarefa buscarTarefaDoUsuario(Long id, User usuario) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new TarefaNaoEncontradaException(id));

        if (!tarefa.getUsuario().getId().equals(usuario.getId())) {
            throw new AcessoNegadoException();
        }

        return tarefa;
    }
}
