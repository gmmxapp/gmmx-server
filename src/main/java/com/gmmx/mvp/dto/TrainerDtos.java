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
    }

    @Data
    public static class TrainerResponse {
        private UUID id;
        private String fullName;
        private String email;
        private String mobile;
        private boolean active;
    }
}
