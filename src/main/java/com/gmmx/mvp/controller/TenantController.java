package com.gmmx.mvp.controller;

import com.gmmx.mvp.dto.ApiResponse;
import com.gmmx.mvp.dto.TenantDtos;
import com.gmmx.mvp.entity.Tenant;
import com.gmmx.mvp.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/tenants", "/tenants"})
@RequiredArgsConstructor
public class TenantController {

    private final TenantRepository tenantRepository;

    @GetMapping("/lookup/{gymId}")
    public ApiResponse<TenantDtos.TenantLookupResponse> lookup(@PathVariable String gymId) {
        Tenant tenant = tenantRepository.findBySubdomain(gymId)
                .orElseThrow(() -> new RuntimeException("Gym not found with ID: " + gymId));

        TenantDtos.TenantLookupResponse response = TenantDtos.TenantLookupResponse.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .subdomain(tenant.getSubdomain())
                .displayName(tenant.getDisplayName() != null ? tenant.getDisplayName() : tenant.getName())
                .logoUrl(tenant.getLogoUrl())
                .address(tenant.getAddress())
                .contactPhone(tenant.getContactMobileNumber())
                .hasMicrosite(tenant.isHasMicrosite())
                .about(tenant.getAbout())
                .themePrimary(tenant.getThemePrimary() != null ? tenant.getThemePrimary() : "#ef4444")
                .build();

        return ApiResponse.success(response, "Gym found");
    }

    @GetMapping("/check-slug/{slug}")
    public ApiResponse<Boolean> checkSlug(@PathVariable String slug) {
        boolean exists = tenantRepository.existsBySubdomain(slug);
        return ApiResponse.success(!exists, exists ? "Slug already taken" : "Slug available");
    }

    @GetMapping("/me")
    public ApiResponse<Object> getMe() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return ApiResponse.error("Not authenticated");
        }
        java.util.Map<String, Object> details = new java.util.HashMap<>();
        details.put("username", auth.getName());
        details.put("authorities", auth.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.toList()));
        return ApiResponse.success(details, "User details retrieved");
    }
}
