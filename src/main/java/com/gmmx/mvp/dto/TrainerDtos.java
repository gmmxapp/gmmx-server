package com.gmmx.mvp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

public class TrainerDtos {

    @Data
    public static class TrainerCreateRequest {
        @NotBlank
        private String fullName;
        private String email;
        private String mobile;
        private String pin;
    }

    @Data
    public static class TrainerUpdateRequest {
        private String fullName;
        private String email;
        private String mobile;
        private Boolean active;
        private String permissions; // Comma-separated
    }

    @Data
    public static class PermissionsUpdateRequest {
        private java.util.List<String> permissions; // List of permission strings
    }

    @Data
    public static class TrainerResponse {
        private UUID id;
        private String fullName;
        private String email;
        private String mobile;
        private boolean active;
        private String status; // For frontend compatibility
        private String role;   // For frontend compatibility
        private String permissions;
    }
}
