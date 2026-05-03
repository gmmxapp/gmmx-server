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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
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
        tenant.setSubdomain(request.getSubdomain().toLowerCase().replaceAll("[^a-z0-9]", ""));
        tenant.setDisplayName(request.getGymName());
        tenant.setAddress(request.getLocation());
        tenant.setHasMicrosite(request.getHasMicrosite() != null ? request.getHasMicrosite() : false);
        
        // Map Plan
        SubscriptionPlan plan = SubscriptionPlan.FREE;
        if (request.getPlanId() != null) {
            String planId = request.getPlanId().toLowerCase();
            if (planId.contains("starter")) plan = SubscriptionPlan.STARTER;
            else if (planId.contains("growth")) plan = SubscriptionPlan.GROWTH;
            else if (planId.contains("scale")) plan = SubscriptionPlan.SCALE;
        }
        tenant.setPlan(plan);
        
        tenant = tenantRepository.save(tenant);

        // 2. Create Owner User
        UserAccount owner = new UserAccount();
        owner.setTenantId(tenant.getId()); 
        owner.setEmail(request.getEmail());
        owner.setCountryCode(request.getCountryCode() != null ? request.getCountryCode() : "+91");
        owner.setMobileNumber(com.gmmx.mvp.util.PhoneUtils.normalizeIdentifier(request.getPhone()));
        owner.setFullName(request.getOwnerName());
        owner.setPasswordHash(passwordEncoder.encode(request.getPin()));
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
        // 1. Resolve Tenant
        Tenant tenant = tenantRepository.findBySubdomain(request.getGymId())
                .orElseThrow(() -> new RuntimeException("Gym not found: " + request.getGymId()));

        // 2. Normalize Identifier
        String originalIdentifier = request.getIdentifier();
        String identifier = com.gmmx.mvp.util.PhoneUtils.normalizeIdentifier(originalIdentifier);
        log.info("Login attempt - Original: [{}], Normalized: [{}], GymId: [{}]", originalIdentifier, identifier, request.getGymId());
        
        // 3. Find User
        // First try finding user in the specific tenant
        UserAccount user = userAccountRepository.findByEmailOrMobileNumberAndTenantId(identifier, identifier, tenant.getId())
                .orElseGet(() -> {
                    log.info("User [{}] not found in tenant [{}]. Searching globally...", identifier, tenant.getSubdomain());
                    // If not found, check globally to see if this user belongs to another gym
                    UserAccount globalUser = userAccountRepository.findByEmailOrMobileNumber(identifier, identifier)
                            .orElseThrow(() -> {
                                log.warn("Global lookup failed for identifier: [{}] after trying tenant: [{}]", identifier, tenant.getId());
                                return new RuntimeException("User not found in this system. Check your credentials.");
                            });
                    
                    log.info("User [{}] found globally with role [{}]. Correct tenant: [{}]", 
                            identifier, globalUser.getRole(), globalUser.getTenantId());
                    
                    // Allow Owners and Trainers to log into their correct tenant even if they started from the wrong gym ID
                    if (globalUser.getRole() == UserRole.OWNER || globalUser.getRole() == UserRole.TRAINER || globalUser.getRole() == UserRole.SUPER_ADMIN) {
                        return globalUser;
                    }
                    
                    // For members, we are stricter to prevent accidental cross-tenant access if they share identifiers
                    throw new RuntimeException("This account belongs to another gym. Please enter your correct Gym ID.");
                });

        // 4. Brute force check
        if (user.isAccountLocked()) {
            throw new RuntimeException("Account is locked due to too many failed attempts. Contact owner.");
        }

        // 5. Verify PIN
        if (!passwordEncoder.matches(request.getPin(), user.getPasswordHash())) {
            handleFailedLogin(user);
            throw new RuntimeException("Invalid PIN.");
        }

        // 6. Device Binding Check
        if (user.getDeviceId() != null && !user.getDeviceId().equals(request.getDeviceId())) {
            System.out.println("Security: Device mismatch for user " + user.getEmail() + ". Attempted from: " + request.getDeviceId());
            // Optionally: throw new RuntimeException("New device detected. Verification required.");
        }
        
        // Update device ID if first time
        if (user.getDeviceId() == null && request.getDeviceId() != null) {
            user.setDeviceId(request.getDeviceId());
            userAccountRepository.save(user); // Save the update
        }

        // Reset failed attempts
        user.setFailedLoginAttempts(0);
        userAccountRepository.save(user);

        // EMERGENCY FIX: Ensure nitheeshms5@gmail.com is an OWNER
        if ("nitheeshms5@gmail.com".equalsIgnoreCase(user.getEmail()) && user.getRole() != UserRole.OWNER) {
            log.info("Promoting nitheeshms5@gmail.com to OWNER role");
            user.setRole(UserRole.OWNER);
            userAccountRepository.save(user);
        }

        return authenticateAndGenerateTokens(user, user.getTenantId());
    }

    private void handleFailedLogin(UserAccount user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        if (attempts >= 5) {
            user.setAccountLocked(true);
        }
        userAccountRepository.save(user);
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

            // EMERGENCY FIX: Ensure nitheeshms5@gmail.com is an OWNER
            if ("nitheeshms5@gmail.com".equalsIgnoreCase(user.getEmail()) && user.getRole() != UserRole.OWNER) {
                log.info("Promoting nitheeshms5@gmail.com to OWNER role (Google Login)");
                user.setRole(UserRole.OWNER);
                userAccountRepository.save(user);
            }

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

    public boolean emailExists(String email) {
        return userAccountRepository.existsByEmail(email);
    }
}
