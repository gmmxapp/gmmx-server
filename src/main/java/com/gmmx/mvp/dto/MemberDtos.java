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
        private UUID assignedTrainerId;
        private UUID membershipPlanId;
        private java.time.LocalDate joinDate;
        private java.time.LocalDate expiryDate;
        private java.math.BigDecimal feesPaid;
        private String feesNotes;
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
        private UUID assignedTrainerId;
        private UUID membershipPlanId;
        private java.time.LocalDate joinDate;
        private java.time.LocalDate expiryDate;
        private java.math.BigDecimal feesPaid;
        private String feesNotes;
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
        private UUID assignedTrainerId;
        private String assignedTrainerName;
        private UUID membershipPlanId;
        private String membershipPlanName;
        private java.time.LocalDate joinDate;
        private java.time.LocalDate expiryDate;
        private java.math.BigDecimal feesPaid;
        private String feesNotes;
    }
}
