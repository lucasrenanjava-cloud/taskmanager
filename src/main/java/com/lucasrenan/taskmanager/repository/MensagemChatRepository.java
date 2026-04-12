package com.lucasrenan.taskmanager.repository;

import com.lucasrenan.taskmanager.model.entity.MensagemChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MensagemChatRepository extends JpaRepository<MensagemChat, Long> {

    List<MensagemChat> findByUsuarioIdOrderByCriadoEmAsc(UUID usuarioId);

    void deleteByUsuarioId(UUID usuarioId);
}
