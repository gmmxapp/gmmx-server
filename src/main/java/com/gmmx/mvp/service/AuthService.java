package com.gmmx.mvp.service;

import com.gmmx.mvp.core.tenant.TenantContext;
import com.gmmx.mvp.dto.AuthDtos;
import com.gmmx.mvp.entity.RefreshToken;
import com.gmmx.mvp.entity.SubscriptionPlan;
import com.gmmx.mvp.entity.Tenant;
import com.gmmx.mvp.entity.UserAccount;
import com.gmmx.mvp.entity.UserRole;
import com.gmmx.mvp.mapper.UserMapper;
import com.gmmx.mvp.repository.TenantRepository;
import com.gmmx.mvp.repository.UserAccountRepository;
import com.gmmx.mvp.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    // Service for authentication

    private final TenantRepository tenantRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;
    private final OtpService otpService;

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if (tenantRepository.existsBySubdomain(request.getSubdomain())) {
            throw new RuntimeException("Subdomain already exists");
        }
        if (userAccountRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // 1. Create Tenant
        Tenant tenant = new Tenant();
        tenant.setName(request.getGymName());
        tenant.setSubdomain(request.getSubdomain());
        tenant.setPlan(SubscriptionPlan.FREE);
        tenant = tenantRepository.save(tenant);

        // 2. Create Owner User
        UserAccount owner = new UserAccount();
        owner.setTenantId(tenant.getId()); // Manual injection for root setup
        owner.setEmail(request.getEmail());
        owner.setMobile(request.getPhone());
        owner.setFullName(request.getOwnerName());
        owner.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        owner.setRole(UserRole.OWNER);
        owner = userAccountRepository.save(owner);

        // 3. Authenticate and Generate Tokens
        return authenticateAndGenerateTokens(owner, tenant.getId());
    }

    public void sendOtp(AuthDtos.SendOtpRequest request) {
        otpService.generateAndSendOtp(request.getIdentifier());
    }

    public void verifyOtp(AuthDtos.VerifyOtpRequest request) {
        if (!otpService.verifyOtp(request.getIdentifier(), request.getOtp())) {
            throw new RuntimeException("Invalid or expired OTP");
        }
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        System.out.println("Login attempt for identifier: [" + request.getIdentifier() + "] with OTP: [" + request.getOtp() + "]");
        
        if (!otpService.verifyOtp(request.getIdentifier(), request.getOtp())) {
            System.out.println("OTP verification failed for identifier: " + request.getIdentifier());
            throw new RuntimeException("Invalid or expired OTP for identifier: " + request.getIdentifier());
        }
        
        String identifier = request.getIdentifier().trim();
        System.out.println("Identifier: [" + identifier + "], length: " + identifier.length());
        for (int i = 0; i < identifier.length(); i++) {
            System.out.println("Char at " + i + ": " + (int)identifier.charAt(i));
        }
        UserAccount user = userAccountRepository.findByEmailOrMobile(identifier, identifier)
                .orElseThrow(() -> {
                    System.out.println("User not found in DB for identifier: [" + identifier + "]");
                    return new RuntimeException("User not found in database for: " + identifier);
                });
        
        System.out.println("User found! ID: " + user.getId() + ", Email: " + user.getEmail() + ", Mobile: " + user.getMobile());
        return authenticateAndGenerateTokens(user, user.getTenantId());
    }

    public AuthDtos.AuthResponse googleLogin(AuthDtos.GoogleLoginRequest request) {
        try {
            com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier verifier = 
                new com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier.Builder(
                    new com.google.api.client.http.javanet.NetHttpTransport(), 
                    new com.google.api.client.json.gson.GsonFactory())
                .build();

            com.google.api.client.googleapis.auth.oauth2.GoogleIdToken idToken = verifier.verify(request.getIdToken());
            if (idToken == null) {
                throw new RuntimeException("Invalid Google ID Token");
            }

            com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();

            UserAccount user = userAccountRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not registered. Please sign up on the web or ask your gym owner to add you."));

            return authenticateAndGenerateTokens(user, user.getTenantId());
        } catch (Exception e) {
            throw new RuntimeException("Google Authentication failed: " + e.getMessage());
        }
    }

    public AuthDtos.AuthResponse refresh(AuthDtos.TokenRefreshRequest request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> authenticateAndGenerateTokens(user, user.getTenantId()))
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    private AuthDtos.AuthResponse authenticateAndGenerateTokens(UserAccount user, UUID tenantId) {
        String accessToken = jwtUtils.generateToken(user, tenantId);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        AuthDtos.AuthResponse response = new AuthDtos.AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken.getToken());
        response.setUser(userMapper.toResponse(user));
        return response;
    }
}
