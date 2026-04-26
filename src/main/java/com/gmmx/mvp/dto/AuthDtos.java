package com.gmmx.mvp.dto;

import com.gmmx.mvp.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

public class AuthDtos {

    @Data
    public static class LoginRequest {
        @NotBlank
        private String gymId;
        @NotBlank
        private String identifier; // email or phone
        @NotBlank
        private String pin;
        private String deviceId;
    }

    @Data
    public static class GoogleLoginRequest {
        @NotBlank
        private String idToken;
    }

    @Data
    public static class SendOtpRequest {
        @NotBlank
        private String identifier; // email or phone
    }

    @Data
    public static class VerifyOtpRequest {
        @NotBlank
        private String identifier;
        @NotBlank
        private String otp;
    }

    @Data
    public static class RegisterRequest {
        @NotBlank
        private String gymName;
        @NotBlank
        private String subdomain;
        @NotBlank
        private String ownerName;
        @NotBlank @Email
        private String email;
        @NotBlank
        private String countryCode;
        @NotBlank
        private String phone;
        @NotBlank @Size(min = 4, max = 4)
        private String pin;
        private String location;
        private Boolean hasMicrosite;
        private String planId;
        private String paymentId;
    }

    @Data
    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
        private UserResponse user;
    }

    @Data
    public static class UserResponse {
        private UUID id;
        private String email;
        private String fullName;
        private String countryCode;
        private String mobile;
        private UserRole role;
        private UUID tenantId;
    }

    @Data
    public static class TokenRefreshRequest {
        @NotBlank
        private String refreshToken;
    }
}
