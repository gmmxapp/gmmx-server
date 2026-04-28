package com.gmmx.mvp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

public class EquipmentDtos {

    @Data
    public static class EquipmentRequest {
        @NotBlank
        private String name;
        @NotNull
        private Integer quantity;
        @NotBlank
        private String condition;
        private LocalDate lastMaintenanceDate;
    }

    @Data
    public static class EquipmentResponse {
        private UUID id;
        private String name;
        private Integer quantity;
        private String condition;
        private LocalDate lastMaintenanceDate;
    }
}
