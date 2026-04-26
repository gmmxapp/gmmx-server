package com.gmmx.mvp.service;

import com.gmmx.mvp.entity.Tenant;
import com.gmmx.mvp.repository.TenantRepository;
import com.gmmx.mvp.repository.UserAccountRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
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
                    overview.setOwnerMobile(owner.getMobile());
                });
                
        return overview;
    }

    @Transactional
    public void deleteGym(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Gym not found"));
        
        if (tenant.getSubdomain().equals("admin")) {
            throw new RuntimeException("Cannot delete admin tenant");
        }

        // Delete all users for this tenant
        userAccountRepository.deleteByTenantId(tenantId);
                
        tenantRepository.delete(tenant);
    }
}
