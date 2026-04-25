package com.gmmx.mvp.dto;

import com.gmmx.mvp.entity.MembershipStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

public class MemberDtos {

    @Data
    public static class MemberCreateRequest {
        @NotBlank
        private String fullName;
        @NotBlank
        private String email;
        private String mobile;
        private Double height;
        private Double weight;
        private String medicalHistory;
        private String goals;
    }

    @Data
    public static class MemberUpdateRequest {
        private String fullName;
        private String email;
        private String mobile;
        private Double height;
        private Double weight;
        private String medicalHistory;
        private String goals;
        private MembershipStatus status;
    }

    @Data
    public static class MemberResponse {
        private UUID id;
        private String fullName;
        private String email;
        private String mobile;
        private Double height;
        private Double weight;
        private String medicalHistory;
        private String goals;
        private MembershipStatus status;
    }
}
