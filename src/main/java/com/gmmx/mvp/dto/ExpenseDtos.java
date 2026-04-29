package com.gmmx.mvp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class ExpenseDtos {

    @Data
    public static class ExpenseRequest {
        @NotBlank
        private String title;
        private String description;
        @NotNull
        private BigDecimal amount;
        @NotNull
        private LocalDate date;
        @NotBlank
        private String category;
        private String paymentMethod;
    }

    @Data
    public static class ExpenseResponse {
        private UUID id;
        private String title;
        private String description;
        private BigDecimal amount;
        private LocalDate date;
        private String category;
        private String paymentMethod;
    }
}
