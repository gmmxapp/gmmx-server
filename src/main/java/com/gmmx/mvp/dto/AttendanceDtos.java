package com.gmmx.mvp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

public class AttendanceDtos {

    @Data
    public static class AttendanceMarkRequest {
        @NotNull
        private UUID memberId;
        private String method = "MANUAL"; // MANUAL, QR, LOCATION, HYBRID
        private Double latitude;
        private Double longitude;
        private String qrToken;
    }

    @Data
    public static class AttendanceResponse {
        private UUID id;
        private UUID memberId;
        private String memberName;
        private LocalDateTime checkIn;
        private LocalDateTime checkOut;
        private String method;
        private java.time.LocalDate date;
    }
}
