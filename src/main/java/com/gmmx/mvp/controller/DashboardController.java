package com.gmmx.mvp.controller;

import com.gmmx.mvp.dto.ApiResponse;
import com.gmmx.mvp.dto.DashboardDtos;
import com.gmmx.mvp.repository.LeadRepository;
import com.gmmx.mvp.repository.MemberProfileRepository;
import com.gmmx.mvp.repository.ExpenseRepository;
import com.gmmx.mvp.repository.PaymentRepository;
import com.gmmx.mvp.core.tenant.TenantContext;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Slf4j
public class DashboardController {

    private final MemberProfileRepository memberRepository;
    private final LeadRepository leadRepository;
    private final ExpenseRepository expenseRepository;
    private final PaymentRepository paymentRepository;

    @GetMapping("/owner/stats")
    // @PreAuthorize("isAuthenticated()")
    public ApiResponse<DashboardDtos.OwnerDashboardStats> getOwnerStats() {
        log.info("Fetching dashboard stats for owner");
        try {
            UUID tenantId = TenantContext.getTenantId();
            log.info("Tenant ID: {}", tenantId);
            var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            log.info("User authorities: {}", auth != null ? auth.getAuthorities() : "NULL");
            
            if (tenantId == null) {
                log.warn("No tenant ID found in context");
                return ApiResponse.error("No tenant context found");
            }

            long totalMembers = memberRepository.countByTenantId(tenantId);
            log.info("Total members: {}", totalMembers);
            long totalLeads = leadRepository.countByTenantId(tenantId);
            log.info("Total leads: {}", totalLeads);
            
            // Format numbers as Strings for Flutter
            String totalMembersStr = String.valueOf(totalMembers);
            String activeTrainersStr = "0"; // To be implemented
            String monthlyRevenueStr = "₹55,000";
            String monthlyExpensesStr = "₹12,000";
            String totalLeadsStr = String.valueOf(totalLeads);
            String newLeadsTodayStr = "0";
            String newMembersThisMonthStr = "+0";

            // Mocking some data for trend until we have enough real records
            DashboardDtos.OwnerDashboardStats stats = DashboardDtos.OwnerDashboardStats.builder()
                    .totalMembers(totalMembersStr)
                    .activeMembers(totalMembersStr)
                    .activeTrainers(activeTrainersStr)
                    .totalLeads(totalLeadsStr)
                    .newLeadsToday(newLeadsTodayStr)
                    .monthlyRevenue(monthlyRevenueStr)
                    .monthlyExpenses(monthlyExpensesStr)
                    .newMembersThisMonth(newMembersThisMonthStr)
                    .attendanceToday(24)
                    .revenueTrend(new ArrayList<>())
                    .weeklyRevenue(new ArrayList<>())
                    .totalWeeklyRevenue(monthlyRevenueStr)
                    .membershipDistribution(new java.util.HashMap<>())
                    .build();
                    
            return ApiResponse.success(stats, "Stats retrieved successfully");
        } catch (Exception e) {
            log.error("Error fetching dashboard stats", e);
            return ApiResponse.error("Failed to fetch dashboard stats: " + e.getMessage());
        }
    }
}
