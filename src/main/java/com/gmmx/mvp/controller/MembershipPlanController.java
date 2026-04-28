package com.gmmx.mvp.controller;

import com.gmmx.mvp.dto.ApiResponse;
import com.gmmx.mvp.dto.MembershipPlanDtos;
import com.gmmx.mvp.service.MembershipPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/membership-plans")
@RequiredArgsConstructor
@Tag(name = "Admin - Membership Plans", description = "Membership plan management for gym owners")
@SecurityRequirement(name = "BearerAuth")
public class MembershipPlanController {

    private final MembershipPlanService planService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Create a new Membership Plan")
    public ApiResponse<MembershipPlanDtos.MembershipPlanResponse> create(@Valid @RequestBody MembershipPlanDtos.MembershipPlanRequest request) {
        return ApiResponse.success(planService.createPlan(request), "Membership Plan created successfully");
    }

    @GetMapping
    @Operation(summary = "List all Membership Plans")
    public ApiResponse<List<MembershipPlanDtos.MembershipPlanResponse>> getAll() {
        return ApiResponse.success(planService.getAllPlans(), "Membership Plans retrieved successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Membership Plan Details")
    public ApiResponse<MembershipPlanDtos.MembershipPlanResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(planService.getPlanById(id), "Membership Plan retrieved successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Update Membership Plan")
    public ApiResponse<MembershipPlanDtos.MembershipPlanResponse> update(@PathVariable UUID id, @Valid @RequestBody MembershipPlanDtos.MembershipPlanRequest request) {
        return ApiResponse.success(planService.updatePlan(id, request), "Membership Plan updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Delete Membership Plan")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        planService.deletePlan(id);
        return ApiResponse.success(null, "Membership Plan deleted successfully");
    }
}
