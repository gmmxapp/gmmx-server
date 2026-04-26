package com.gmmx.mvp.controller;

import com.gmmx.mvp.dto.ApiResponse;
import com.gmmx.mvp.service.SuperAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
@Tag(name = "System Admin", description = "Global management for Gmmx Platform")
@SecurityRequirement(name = "BearerAuth")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    @GetMapping("/gyms")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "List all Gyms", description = "Returns an overview of all registered gyms, their plans, and user counts.")
    public ApiResponse<List<SuperAdminService.GymOverview>> getAllGyms() {
        return ApiResponse.success(superAdminService.getAllGyms(), "Gyms retrieved successfully");
    }

    @DeleteMapping("/gyms/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete Gym", description = "Permanently deletes a gym and all its associated users.")
    public ApiResponse<Void> deleteGym(@PathVariable UUID id) {
        superAdminService.deleteGym(id);
        return ApiResponse.success(null, "Gym deleted successfully");
    }
}
