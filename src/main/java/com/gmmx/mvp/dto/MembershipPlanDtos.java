package com.gmmx.mvp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

public class MembershipPlanDtos {

    @Data
    public static class MembershipPlanRequest {
        @NotBlank
        private String name;
        @NotNull
        private Integer durationDays;
        @NotNull
        private BigDecimal price;
        private String description;
    }

    @Data
    public static class MembershipPlanResponse {
        private UUID id;
        private String name;
        private Integer durationDays;
        private BigDecimal price;
        private String description;
    }
}
