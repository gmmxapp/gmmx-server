package com.gmmx.mvp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChatDtos {

    @Data
    public static class ChatMessageRequest {
        @NotNull
        private String recipientId;
        @NotBlank
        private String message;
    }

    @Data
    public static class ChatMessageResponse {
        private UUID id;
        private UUID senderId;
        private String senderName;
        private UUID recipientId;
        private String recipientName;
        private String message;
        private boolean read;
        private LocalDateTime createdAt;
    }
}
