package com.gmmx.mvp.controller;

import com.gmmx.mvp.dto.ApiResponse;
import com.gmmx.mvp.dto.EquipmentDtos;
import com.gmmx.mvp.service.EquipmentService;
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
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
@Tag(name = "Admin - Equipment", description = "Equipment management for gym owners")
@SecurityRequirement(name = "BearerAuth")
public class EquipmentController {

    private final EquipmentService equipmentService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Add new Equipment")
    public ApiResponse<EquipmentDtos.EquipmentResponse> create(@Valid @RequestBody EquipmentDtos.EquipmentRequest request) {
        return ApiResponse.success(equipmentService.createEquipment(request), "Equipment added successfully");
    }

    @GetMapping
    @Operation(summary = "List all Equipment")
    public ApiResponse<List<EquipmentDtos.EquipmentResponse>> getAll() {
        return ApiResponse.success(equipmentService.getAllEquipment(), "Equipment retrieved successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Equipment Details")
    public ApiResponse<EquipmentDtos.EquipmentResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(equipmentService.getEquipmentById(id), "Equipment retrieved successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Update Equipment")
    public ApiResponse<EquipmentDtos.EquipmentResponse> update(@PathVariable UUID id, @Valid @RequestBody EquipmentDtos.EquipmentRequest request) {
        return ApiResponse.success(equipmentService.updateEquipment(id, request), "Equipment updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Delete Equipment")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        equipmentService.deleteEquipment(id);
        return ApiResponse.success(null, "Equipment deleted successfully");
    }
}
