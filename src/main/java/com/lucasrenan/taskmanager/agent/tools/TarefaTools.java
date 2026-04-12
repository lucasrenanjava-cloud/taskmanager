package com.lucasrenan.taskmanager.agent.tools;

import com.lucasrenan.taskmanager.model.entity.Tarefa;
import com.lucasrenan.taskmanager.model.entity.User;
import com.lucasrenan.taskmanager.model.enums.Prioridade;
import com.lucasrenan.taskmanager.model.enums.StatusTarefa;
import com.lucasrenan.taskmanager.repository.TarefaRepository;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
public class TarefaTools {

    private final TarefaRepository tarefaRepository;
    private final ThreadLocal<User> usuarioAtual = new ThreadLocal<>();

    public TarefaTools(TarefaRepository tarefaRepository) {
        this.tarefaRepository = tarefaRepository;
    }

    public void setUsuarioAtual(User usuario) {
        usuarioAtual.set(usuario);
    }

    public void limparUsuarioAtual() {
        usuarioAtual.remove();
    }

    private User getUsuario() {
        User usuario = usuarioAtual.get();
        if (usuario == null) {
            throw new IllegalStateException("Usuário não autenticado no contexto do agente");
        }
        return usuario;
    }

    @Tool("Cria uma nova tarefa para o usuário. Use quando o usuário pedir para criar, adicionar ou cadastrar uma tarefa.")
    public String criarTarefa(
            @P("Título da tarefa") String titulo,
            @P("Descrição da tarefa, pode ser nula") String descricao,
            @P("Prioridade: BAIXA, MEDIA ou ALTA. Padrão MEDIA") String prioridade,
            @P("Prazo no formato yyyy-MM-dd, pode ser nulo") String prazo) {

        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(titulo);
        tarefa.setDescricao(descricao);
        tarefa.setUsuario(getUsuario());
        tarefa.setStatus(StatusTarefa.PENDENTE);

        try {
            tarefa.setPrioridade(Prioridade.valueOf(prioridade.toUpperCase()));
        } catch (IllegalArgumentException e) {
            tarefa.setPrioridade(Prioridade.MEDIA);
        }

        if (prazo != null && !prazo.isBlank()) {
            try {
                tarefa.setPrazo(LocalDate.parse(prazo));
            } catch (DateTimeParseException e) {
                return "Prazo inválido. Use o formato yyyy-MM-dd, por exemplo: 2026-05-30.";
            }
        }

        tarefaRepository.save(tarefa);
        return "Tarefa '" + titulo + "' criada com sucesso com prioridade " + tarefa.getPrioridade() + ".";
    }

    @Tool("Lista todas as tarefas do usuário. Use quando o usuário pedir para ver, listar ou mostrar suas tarefas.")
    public String listarTarefas() {
        List<Tarefa> tarefas = tarefaRepository.findByUsuarioId(
                getUsuario().getId(), Pageable.unpaged()).getContent();

        if (tarefas.isEmpty()) {
            return "Você não tem nenhuma tarefa cadastrada.";
        }

        StringBuilder sb = new StringBuilder("Suas tarefas:\n");
        for (Tarefa t : tarefas) {
            sb.append(String.format("- [%d] %s | Status: %s | Prioridade: %s",
                    t.getId(), t.getTitulo(), t.getStatus(), t.getPrioridade()));
            if (t.getPrazo() != null) {
                sb.append(" | Prazo: ").append(t.getPrazo());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Tool("Atualiza o status de uma tarefa. Use quando o usuário pedir para marcar, concluir, iniciar ou atualizar o status de uma tarefa.")
    public String atualizarStatus(
            @P("ID numérico da tarefa") Long id,
            @P("Novo status: PENDENTE, EM_ANDAMENTO ou CONCLUIDA") String status) {

        Tarefa tarefa = tarefaRepository.findById(id).orElse(null);

        if (tarefa == null) {
            return "Tarefa com id " + id + " não encontrada.";
        }

        if (!tarefa.getUsuario().getId().equals(getUsuario().getId())) {
            return "Você não tem permissão para alterar esta tarefa.";
        }

        try {
            tarefa.setStatus(StatusTarefa.valueOf(status.toUpperCase()));
            tarefaRepository.save(tarefa);
            return "Status da tarefa '" + tarefa.getTitulo() + "' atualizado para " + tarefa.getStatus() + ".";
        } catch (IllegalArgumentException e) {
            return "Status inválido. Use: PENDENTE, EM_ANDAMENTO ou CONCLUIDA.";
        }
    }

    @Tool("Lista as tarefas atrasadas do usuário, ou seja, com prazo vencido e não concluídas.")
    public String listarAtrasadas() {
        List<Tarefa> atrasadas = tarefaRepository.findByUsuarioIdAndPrazoBeforeAndStatusNot(
                getUsuario().getId(), LocalDate.now(), StatusTarefa.CONCLUIDA);

        if (atrasadas.isEmpty()) {
            return "Você não tem tarefas atrasadas.";
        }

        StringBuilder sb = new StringBuilder("Tarefas atrasadas:\n");
        for (Tarefa t : atrasadas) {
            sb.append(String.format("- [%d] %s | Prazo: %s | Status: %s\n",
                    t.getId(), t.getTitulo(), t.getPrazo(), t.getStatus()));
        }
        return sb.toString();
    }
}