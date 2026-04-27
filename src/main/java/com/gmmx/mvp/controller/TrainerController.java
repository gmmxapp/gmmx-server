package com.gmmx.mvp.controller;

import com.gmmx.mvp.dto.ApiResponse;
import com.gmmx.mvp.dto.TrainerDtos;
import com.gmmx.mvp.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
@Tag(name = "Admin - Trainers", description = "Trainer management for gym owners")
@SecurityRequirement(name = "BearerAuth")
public class TrainerController {

    private final TrainerService trainerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'SUPER_ADMIN')")
    @Operation(summary = "Create a new Trainer", description = "Creates a UserAccount with role TRAINER.")
    public ApiResponse<TrainerDtos.TrainerResponse> create(@Valid @RequestBody TrainerDtos.TrainerCreateRequest request) {
        return ApiResponse.success(trainerService.createTrainer(request), "Trainer created successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'SUPER_ADMIN')")
    @Operation(summary = "Update Trainer", description = "Updates an existing trainer's details.")
    public ApiResponse<TrainerDtos.TrainerResponse> update(@PathVariable UUID id, @Valid @RequestBody TrainerDtos.TrainerUpdateRequest request) {
        return ApiResponse.success(trainerService.updateTrainer(id, request), "Trainer updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'SUPER_ADMIN')")
    @Operation(summary = "Delete Trainer", description = "Deletes a trainer from the system.")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        trainerService.deleteTrainer(id);
        return ApiResponse.success(null, "Trainer deleted successfully");
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'SUPER_ADMIN')")
    @Operation(summary = "List all Trainers", description = "Returns a paginated list of all trainers in the current gym.")
    public ApiResponse<Page<TrainerDtos.TrainerResponse>> getAll(Pageable pageable) {
        return ApiResponse.success(trainerService.getAllTrainers(pageable), "Trainers retrieved successfully");
    }
}
