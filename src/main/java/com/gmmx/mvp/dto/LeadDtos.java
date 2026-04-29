package com.gmmx.mvp.dto;

import com.gmmx.mvp.entity.LeadSource;
import com.gmmx.mvp.entity.LeadStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

public class LeadDtos {

    @Data
    public static class LeadCreateRequest {
        private String fullName;
        private String mobile;
        private String email;
        private String notes;
        private LeadSource source;
        private String interestLevel;
        private UUID assignedTrainerId;
        private java.time.LocalDate trialDate;
    }

    @Data
    public static class LeadResponse {
        private UUID id;
        private String fullName;
        private String mobile;
        private String email;
        private String notes;
        private LeadStatus status;
        private LeadSource source;
        private String interestLevel;
        private UUID assignedTrainerId;
        private String assignedTrainerName;
        private java.time.LocalDate trialDate;
        private boolean isTrialCompleted;
        private LocalDateTime createdAt;
    }

    @Data
    public static class LeadStatusUpdateRequest {
        private LeadStatus status;
    }
}
