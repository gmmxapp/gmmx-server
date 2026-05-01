package com.gmmx.mvp.service;

import com.gmmx.mvp.core.tenant.TenantContext;
import com.gmmx.mvp.dto.ChatDtos;
import com.gmmx.mvp.entity.ChatMessage;
import com.gmmx.mvp.entity.UserAccount;
import com.gmmx.mvp.exception.BadRequestException;
import com.gmmx.mvp.exception.ResourceNotFoundException;
import com.gmmx.mvp.repository.ChatMessageRepository;
import com.gmmx.mvp.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatMessageRepository chatRepository;
    private final UserAccountRepository userRepository;
    private final com.gmmx.mvp.repository.FcmTokenRepository fcmTokenRepository;

    @Transactional
    public ChatDtos.ChatMessageResponse sendMessage(ChatDtos.ChatMessageRequest request) {
        String senderEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Message attempt by sender: {} to recipient: {}", senderEmail, request.getRecipientId());
        
        UserAccount sender = userRepository.findByEmailAndTenantId(senderEmail, TenantContext.getTenantId())
                .orElseThrow(() -> {
                    log.error("Sender not found in tenant: {} for email: {}", TenantContext.getTenantId(), senderEmail);
                    return new ResourceNotFoundException("Sender not found");
                });

        UUID recipientId;
        try {
            recipientId = UUID.fromString(request.getRecipientId());
        } catch (IllegalArgumentException e) {
            log.error("Invalid recipient ID format: {}", request.getRecipientId());
            throw new BadRequestException("Invalid recipient ID format: " + request.getRecipientId());
        }

        UserAccount recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> {
                    log.error("Recipient not found for ID: {}", recipientId);
                    return new ResourceNotFoundException("Recipient not found");
                });

        // Ensure both users belong to the same tenant (simple check)
        if (sender.getTenantId() == null || recipient.getTenantId() == null) {
            log.error("Missing tenant ID. Sender tenant: {}, Recipient tenant: {}", sender.getTenantId(), recipient.getTenantId());
            throw new BadRequestException("Tenant assignment missing for one of the users");
        }

        if (!sender.getTenantId().equals(recipient.getTenantId())) {
            log.error("Tenant mismatch! Sender tenant: {}, Recipient tenant: {}", sender.getTenantId(), recipient.getTenantId());
            throw new BadRequestException("Cross-tenant messaging is not allowed");
        }

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setMessage(request.getMessage());
        message.setRead(false);
        message.setTenantId(sender.getTenantId());

        ChatMessage saved = chatRepository.save(message);

        // Trigger push notification
        sendNotification(recipient, sender.getFullName(), request.getMessage());

        return toResponse(saved);
    }

    private void sendNotification(UserAccount recipient, String senderName, String message) {
        fcmTokenRepository.findById(recipient.getId()).ifPresent(fcmToken -> {
            // TODO: Call actual Firebase service here
            System.out.println("Sending FCM notification to " + recipient.getEmail() + " (Token: " + fcmToken.getToken() + ")");
            System.out.println("Title: New message from " + senderName);
            System.out.println("Body: " + message);
        });
    }

    @Transactional
    public void saveFcmToken(UUID userId, String token) {
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        com.gmmx.mvp.entity.FcmToken fcmToken = fcmTokenRepository.findById(userId)
                .orElse(new com.gmmx.mvp.entity.FcmToken());
        
        fcmToken.setUser(user);
        fcmToken.setToken(token);
        fcmToken.setUpdatedAt(java.time.LocalDateTime.now());
        
        fcmTokenRepository.save(fcmToken);
    }

    public Page<ChatDtos.ChatMessageResponse> getConversation(UUID otherUserId, Pageable pageable) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAccount currentUser = userRepository.findByEmailAndTenantId(currentUserEmail, TenantContext.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (pageable == null || pageable.getSort().isUnsorted()) {
            pageable = org.springframework.data.domain.PageRequest.of(
                pageable != null ? pageable.getPageNumber() : 0,
                pageable != null ? pageable.getPageSize() : 20,
                org.springframework.data.domain.Sort.by("createdAt").descending()
            );
        }

        return chatRepository.findConversation(currentUser.getId(), otherUserId, pageable)
                .map(this::toResponse);
    }

    private ChatDtos.ChatMessageResponse toResponse(ChatMessage msg) {
        ChatDtos.ChatMessageResponse res = new ChatDtos.ChatMessageResponse();
        res.setId(msg.getId());
        res.setSenderId(msg.getSender().getId());
        res.setSenderName(msg.getSender().getFullName());
        res.setRecipientId(msg.getRecipient().getId());
        res.setRecipientName(msg.getRecipient().getFullName());
        res.setMessage(msg.getMessage());
        res.setRead(msg.isRead());
        res.setCreatedAt(msg.getCreatedAt());
        return res;
    }
}
