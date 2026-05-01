package com.gmmx.mvp.controller;

import com.gmmx.mvp.dto.ApiResponse;
import com.gmmx.mvp.dto.DashboardDtos;
import com.gmmx.mvp.service.DashboardService;
import com.gmmx.mvp.entity.UserAccount;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/owner/stats")
    public ApiResponse<DashboardDtos.OwnerStatsResponse> getOwnerStats() {
        log.info("Fetching real dashboard stats for owner");
        try {
            DashboardDtos.OwnerStatsResponse stats = dashboardService.getOwnerStats();
            return ApiResponse.success(stats, "Stats retrieved successfully");
        } catch (Exception e) {
            log.error("Error fetching dashboard stats", e);
            return ApiResponse.error("Failed to fetch dashboard stats: " + e.getMessage());
        }
    }

    @GetMapping("/recent-activity")
    public ApiResponse<List<DashboardDtos.RecentActivityResponse>> getRecentActivity() {
        try {
            return ApiResponse.success(dashboardService.getRecentActivity(), "Recent activity retrieved");
        } catch (Exception e) {
            log.error("Error fetching recent activity", e);
            return ApiResponse.error("Failed to fetch recent activity: " + e.getMessage());
        }
    }

    @GetMapping("/client/stats")
    public ApiResponse<DashboardDtos.ClientStatsResponse> getClientStats(@AuthenticationPrincipal UserAccount user) {
        log.info("Fetching real dashboard stats for member: {}", user.getEmail());
        try {
            return ApiResponse.success(dashboardService.getClientStats(user), "Member stats retrieved");
        } catch (Exception e) {
            log.error("Error fetching member stats", e);
            return ApiResponse.error("Failed to fetch member stats: " + e.getMessage());
        }
    }
}
