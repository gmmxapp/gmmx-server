package com.gmmx.mvp.service;

import com.gmmx.mvp.entity.Tenant;
import com.gmmx.mvp.repository.TenantRepository;
import com.gmmx.mvp.repository.UserAccountRepository;
import com.gmmx.mvp.dto.AuthDtos;
import com.gmmx.mvp.mapper.UserMapper;
import com.gmmx.mvp.entity.UserAccount;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuperAdminService {

    private final TenantRepository tenantRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Data
    public static class GymOverview {
        private UUID id;
        private String name;
        private String subdomain;
        private String plan;
        private long userCount;
        private String ownerEmail;
        private String ownerMobile;
    }

    public List<GymOverview> getAllGyms() {
        return tenantRepository.findAll().stream()
                .filter(t -> !t.getSubdomain().equals("admin"))
                .map(this::mapToOverview)
                .collect(Collectors.toList());
    }

    private GymOverview mapToOverview(Tenant tenant) {
        GymOverview overview = new GymOverview();
        overview.setId(tenant.getId());
        overview.setName(tenant.getName());
        overview.setSubdomain(tenant.getSubdomain());
        overview.setPlan(tenant.getPlan().name());
        overview.setUserCount(userAccountRepository.countByTenantId(tenant.getId()));
        
        // Find owner
        userAccountRepository.findByTenantIdAndRole(tenant.getId(), com.gmmx.mvp.entity.UserRole.OWNER)
                .ifPresent(owner -> {
                    overview.setOwnerEmail(owner.getEmail());
                    overview.setOwnerMobile(owner.getMobileNumber());
                });
                
        return overview;
    }

    public List<AuthDtos.UserResponse> getGymUsers(UUID tenantId) {
        return userAccountRepository.findAllByTenantId(tenantId).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AuthDtos.UserResponse addUserToGym(UUID tenantId, AuthDtos.RegisterRequest request) {
        UserAccount user = new UserAccount();
        user.setTenantId(tenantId);
        user.setEmail(request.getEmail());
        user.setFullName(request.getOwnerName()); // Reuse ownerName field for full name
        user.setCountryCode(request.getCountryCode() != null ? request.getCountryCode() : "+91");
        user.setMobileNumber(com.gmmx.mvp.util.PhoneUtils.normalizeIdentifier(request.getPhone()));
        user.setPasswordHash(passwordEncoder.encode(request.getPin()));
        user.setRole(com.gmmx.mvp.entity.UserRole.MEMBER); // Default to member if not specified
        
        user = userAccountRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Transactional
    public void resetUserPin(UUID userId, String newPin) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPasswordHash(passwordEncoder.encode(newPin));
        userAccountRepository.save(user);
    }
}
