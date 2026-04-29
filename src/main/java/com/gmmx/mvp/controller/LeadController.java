package com.gmmx.mvp.controller;

import com.gmmx.mvp.dto.ApiResponse;
import com.gmmx.mvp.dto.LeadDtos;
import com.gmmx.mvp.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'TRAINER')")
    public ApiResponse<LeadDtos.LeadResponse> createLead(@RequestBody LeadDtos.LeadCreateRequest request) {
        return ApiResponse.success(leadService.createLead(request), "Lead created successfully");
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'TRAINER')")
    public ApiResponse<Page<LeadDtos.LeadResponse>> getAllLeads(Pageable pageable) {
        return ApiResponse.success(leadService.getAllLeads(pageable), "Leads fetched successfully");
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('OWNER', 'TRAINER')")
    public ApiResponse<LeadDtos.LeadResponse> updateStatus(
            @PathVariable UUID id,
            @RequestBody LeadDtos.LeadStatusUpdateRequest request) {
        return ApiResponse.success(leadService.updateStatus(id, request), "Lead status updated");
    }
}
