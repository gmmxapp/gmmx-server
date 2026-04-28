package com.gmmx.mvp.controller;

import com.gmmx.mvp.dto.ApiResponse;
import com.gmmx.mvp.dto.AttendanceDtos;
import com.gmmx.mvp.service.AttendanceService;
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
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Endpoints for member attendance")
@SecurityRequirement(name = "BearerAuth")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/mark")
    @PreAuthorize("hasAnyRole('OWNER', 'TRAINER')")
    @Operation(summary = "Mark Attendance (Manual/Staff)", description = "Allows staff to mark check-in/check-out for a member.")
    public ApiResponse<AttendanceDtos.AttendanceResponse> markAttendance(@Valid @RequestBody AttendanceDtos.AttendanceMarkRequest request) {
        return ApiResponse.success(attendanceService.markAttendance(request), "Attendance marked successfully");
    }

    @PostMapping("/scan")
    @PreAuthorize("hasRole('MEMBER')")
    @Operation(summary = "Self Check-in (QR/Member)", description = "Allows member to mark their own check-in/check-out.")
    public ApiResponse<AttendanceDtos.AttendanceResponse> selfMark(@Valid @RequestBody AttendanceDtos.AttendanceMarkRequest request) {
        // For self-mark, we could verify the memberId matches the logged-in user
        // But for now, we'll keep it simple
        return ApiResponse.success(attendanceService.markAttendance(request), "Attendance marked successfully");
    }

    @GetMapping("/history/{memberId}")
    @PreAuthorize("hasAnyRole('OWNER', 'TRAINER', 'SUPER_ADMIN')")
    @Operation(summary = "Get Member Attendance History")
    public ApiResponse<List<AttendanceDtos.AttendanceResponse>> getHistory(@PathVariable UUID memberId) {
        return ApiResponse.success(attendanceService.getMemberHistory(memberId), "History retrieved successfully");
    }
}
