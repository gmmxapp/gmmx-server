package com.gmmx.mvp.repository;

import com.gmmx.mvp.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    @Query("SELECT m FROM ChatMessage m WHERE (m.sender.id = :u1 AND m.recipient.id = :u2) OR (m.sender.id = :u2 AND m.recipient.id = :u1) ORDER BY m.createdAt DESC")
    Page<ChatMessage> findConversation(UUID u1, UUID u2, Pageable pageable);
}
