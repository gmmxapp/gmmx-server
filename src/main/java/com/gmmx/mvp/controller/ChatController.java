package com.gmmx.mvp.controller;

import com.gmmx.mvp.dto.ApiResponse;
import com.gmmx.mvp.dto.ChatDtos;
import com.gmmx.mvp.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Messaging", description = "Endpoints for in-app chat")
@SecurityRequirement(name = "BearerAuth")
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/send")
    @Operation(summary = "Send a message")
    public ApiResponse<ChatDtos.ChatMessageResponse> sendMessage(@RequestBody ChatDtos.ChatMessageRequest request) {
        log.info("Sending message to recipient: {}", request.getRecipientId());
        log.debug("Message content: {}", request.getMessage());
        return ApiResponse.success(chatService.sendMessage(request), "Message sent");
    }

    @GetMapping("/conversation/{userId}")
    @Operation(summary = "Get conversation with a user")
    public ApiResponse<Page<ChatDtos.ChatMessageResponse>> getConversation(
            @PathVariable UUID userId,
            org.springframework.data.domain.Pageable pageable) {
        log.info("Fetching conversation for user: {}", userId);
        return ApiResponse.success(chatService.getConversation(userId, pageable), "Conversation retrieved");
    }
}
