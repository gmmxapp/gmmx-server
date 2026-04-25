package com.gmmx.mvp.controller;

import com.gmmx.mvp.dto.ApiResponse;
import com.gmmx.mvp.dto.AuthDtos;
import com.gmmx.mvp.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {
    // Endpoints for authentication

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new Gym Owner and Tenant", description = "Atomic operation that creates both the Gym Tenant and the Owner user.")
    public ApiResponse<AuthDtos.AuthResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        return ApiResponse.success(authService.register(request), "Registration successful");
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticates user and returns Access and Refresh tokens.")
    public ApiResponse<AuthDtos.AuthResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return ApiResponse.success(authService.login(request), "Login successful");
    }

    @PostMapping("/google")
    @Operation(summary = "Google Login", description = "Authenticates user with Google email and returns tokens.")
    public ApiResponse<AuthDtos.AuthResponse> googleLogin(@Valid @RequestBody AuthDtos.GoogleLoginRequest request) {
        return ApiResponse.success(authService.googleLogin(request), "Google Login successful");
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Access Token", description = "Uses a valid Refresh Token to generate a new Access Token.")
    public ApiResponse<AuthDtos.AuthResponse> refresh(@Valid @RequestBody AuthDtos.TokenRefreshRequest request) {
        return ApiResponse.success(authService.refresh(request), "Token refreshed successfully");
    }
    @PostMapping("/send-otp")
    @Operation(summary = "Send OTP", description = "Generates and sends an OTP to the given email or phone number.")
    public ApiResponse<Void> sendOtp(@Valid @RequestBody AuthDtos.SendOtpRequest request) {
        authService.sendOtp(request);
        return ApiResponse.success(null, "OTP sent successfully");
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP", description = "Verifies the OTP for the given email or phone number.")
    public ApiResponse<Void> verifyOtp(@Valid @RequestBody AuthDtos.VerifyOtpRequest request) {
        authService.verifyOtp(request);
        return ApiResponse.success(null, "OTP verified successfully");
    }
}
