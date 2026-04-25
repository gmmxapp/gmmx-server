package com.gmmx.mvp.controller;

import com.gmmx.mvp.dto.ApiResponse;
import com.gmmx.mvp.dto.MemberDtos;
import com.gmmx.mvp.service.MemberService;
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
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Admin - Members", description = "Member management for gym owners and trainers")
@SecurityRequirement(name = "BearerAuth")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Create a new Member", description = "Creates a UserAccount and MemberProfile for a new gym member.")
    public ApiResponse<MemberDtos.MemberResponse> create(@Valid @RequestBody MemberDtos.MemberCreateRequest request) {
        return ApiResponse.success(memberService.createMember(request), "Member created successfully");
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'TRAINER')")
    @Operation(summary = "List all Members", description = "Returns a paginated list of all members in the current gym.")
    public ApiResponse<Page<MemberDtos.MemberResponse>> getAll(Pageable pageable) {
        return ApiResponse.success(memberService.getAllMembers(pageable), "Members retrieved successfully");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'TRAINER')")
    @Operation(summary = "Get Member Details", description = "Returns detailed profile of a specific member.")
    public ApiResponse<MemberDtos.MemberResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(memberService.getMemberById(id), "Member retrieved successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Update Member", description = "Updates an existing member's details.")
    public ApiResponse<MemberDtos.MemberResponse> update(@PathVariable UUID id, @Valid @RequestBody MemberDtos.MemberUpdateRequest request) {
        return ApiResponse.success(memberService.updateMember(id, request), "Member updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Delete Member", description = "Deletes a member from the system.")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        memberService.deleteMember(id);
        return ApiResponse.success(null, "Member deleted successfully");
    }
}
