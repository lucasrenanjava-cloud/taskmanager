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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @InjectMocks
    private TarefaService tarefaService;

    @Mock
    private TarefaRepository tarefaRepository;

    private User usuario;
    private Tarefa tarefa;

    @BeforeEach
    void setUp() {
        usuario = new User();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Lucas");
        usuario.setEmail("lucas@email.com");
        usuario.setSenha("123456");

        tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setTitulo("Estudar Spring Boot");
        tarefa.setDescricao("Revisar documentação");
        tarefa.setPrioridade(Prioridade.ALTA);
        tarefa.setStatus(StatusTarefa.PENDENTE);
        tarefa.setPrazo(LocalDate.now().plusDays(7));
        tarefa.setUsuario(usuario);
    }

    @Test
    void deveCriarTarefaComSucesso() {
        TarefaRequest request = new TarefaRequest("Estudar Spring Boot", "Revisar documentação", Prioridade.ALTA, LocalDate.now().plusDays(7));

        when(tarefaRepository.save(any(Tarefa.class))).thenReturn(tarefa);

        TarefaResponse response = tarefaService.criar(request, usuario);

        assertNotNull(response);
        assertEquals("Estudar Spring Boot", response.titulo());
        assertEquals(Prioridade.ALTA, response.prioridade());
        assertEquals(StatusTarefa.PENDENTE, response.status());
        verify(tarefaRepository, times(1)).save(any(Tarefa.class));
    }

    @Test
    void deveCriarTarefaComPrioridadeMediaQuandoNaoInformada() {
        TarefaRequest request = new TarefaRequest("Estudar Spring Boot", "Revisar documentação", null, null);

        tarefa.setPrioridade(Prioridade.MEDIA);
        when(tarefaRepository.save(any(Tarefa.class))).thenReturn(tarefa);

        TarefaResponse response = tarefaService.criar(request, usuario);

        assertEquals(Prioridade.MEDIA, response.prioridade());
    }

    @Test
    void deveBuscarTarefaPorIdComSucesso() {
        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));

        TarefaResponse response = tarefaService.buscarPorId(1L, usuario);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Estudar Spring Boot", response.titulo());
    }

    @Test
    void deveLancarExcecaoQuandoTarefaNaoEncontrada() {
        when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TarefaNaoEncontradaException.class,
                () -> tarefaService.buscarPorId(99L, usuario));
    }

    @Test
    void deveLancarExcecaoQuandoTarefaPertenceAOutroUsuario() {
        User outroUsuario = new User();
        outroUsuario.setId(UUID.randomUUID());

        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));

        assertThrows(AcessoNegadoException.class,
                () -> tarefaService.buscarPorId(1L, outroUsuario));
    }

    @Test
    void deveAtualizarStatusComSucesso() {
        AtualizarStatusRequest request = new AtualizarStatusRequest(StatusTarefa.EM_ANDAMENTO);

        tarefa.setStatus(StatusTarefa.EM_ANDAMENTO);
        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        when(tarefaRepository.save(any(Tarefa.class))).thenReturn(tarefa);

        TarefaResponse response = tarefaService.atualizarStatus(1L, request, usuario);

        assertEquals(StatusTarefa.EM_ANDAMENTO, response.status());
        verify(tarefaRepository, times(1)).save(any(Tarefa.class));
    }

    @Test
    void deveDeletarTarefaComSucesso() {
        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));

        tarefaService.deletar(1L, usuario);

        verify(tarefaRepository, times(1)).delete(tarefa);
    }

    @Test
    void deveLancarExcecaoAoDeletarTarefaDeOutroUsuario() {
        User outroUsuario = new User();
        outroUsuario.setId(UUID.randomUUID());

        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));

        assertThrows(AcessoNegadoException.class,
                () -> tarefaService.deletar(1L, outroUsuario));

        verify(tarefaRepository, never()).delete(any(Tarefa.class));
    }

    @Test
    void deveListarTarefasAtrasadas() {
        Tarefa tarefaAtrasada = new Tarefa();
        tarefaAtrasada.setId(2L);
        tarefaAtrasada.setTitulo("Tarefa atrasada");
        tarefaAtrasada.setStatus(StatusTarefa.PENDENTE);
        tarefaAtrasada.setPrazo(LocalDate.now().minusDays(3));
        tarefaAtrasada.setUsuario(usuario);

        when(tarefaRepository.findByUsuarioIdAndPrazoBeforeAndStatusNot(
                eq(usuario.getId()), any(LocalDate.class), eq(StatusTarefa.CONCLUIDA)))
                .thenReturn(List.of(tarefaAtrasada));

        List<TarefaResponse> response = tarefaService.listarAtrasadas(usuario);

        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
        assertEquals("Tarefa atrasada", response.get(0).titulo());
    }
}