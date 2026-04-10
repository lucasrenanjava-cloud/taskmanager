package com.lucasrenan.taskmanager.repository;

import com.lucasrenan.taskmanager.model.entity.Tarefa;
import com.lucasrenan.taskmanager.model.enums.Prioridade;
import com.lucasrenan.taskmanager.model.enums.StatusTarefa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    Page<Tarefa> findByUsuarioId(UUID usuarioId, Pageable pageable);

    List<Tarefa> findByUsuarioIdAndStatus(UUID usuarioId, StatusTarefa status);

    List<Tarefa> findByUsuarioIdAndPrioridade(UUID usuarioId, Prioridade prioridade);

    List<Tarefa> findByUsuarioIdAndPrazoBeforeAndStatusNot(UUID usuarioId, LocalDate data, StatusTarefa status);

    List<Tarefa> findByUsuarioIdAndTituloContainingIgnoreCase(UUID usuarioId, String titulo);
}
