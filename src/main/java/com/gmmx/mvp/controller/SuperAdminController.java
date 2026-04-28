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

    @GetMapping("/gyms/{id}/users")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "List Gym Users", description = "Returns all users (members, trainers, owners) for a specific gym.")
    public ApiResponse<List<com.gmmx.mvp.dto.AuthDtos.UserResponse>> getGymUsers(@PathVariable UUID id) {
        return ApiResponse.success(superAdminService.getGymUsers(id), "Users retrieved successfully");
    }

    @PostMapping("/gyms/{id}/users")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Add User to Gym", description = "Adds a new user (member) to a specific gym.")
    public ApiResponse<com.gmmx.mvp.dto.AuthDtos.UserResponse> addUserToGym(
            @PathVariable UUID id, 
            @RequestBody com.gmmx.mvp.dto.AuthDtos.RegisterRequest request) {
        return ApiResponse.success(superAdminService.addUserToGym(id, request), "User added successfully");
    }

    @DeleteMapping("/gyms/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete Gym", description = "Permanently removes a gym and ALL its associated data (users, members, equipment, etc.).")
    public ApiResponse<Void> deleteGym(@PathVariable("id") String id) {
        try {
            superAdminService.deleteGym(UUID.fromString(id));
            return ApiResponse.success(null, "Gym deleted successfully");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error("Invalid Gym ID format", 400);
        }
    }

    @PutMapping("/users/{userId}/reset-pin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Reset User PIN", description = "Changes the 4-digit PIN for a user.")
    public ApiResponse<Void> resetPin(@PathVariable UUID userId, @RequestBody String newPin) {
        superAdminService.resetUserPin(userId, newPin);
        return ApiResponse.success(null, "PIN reset successfully");
    }
}
