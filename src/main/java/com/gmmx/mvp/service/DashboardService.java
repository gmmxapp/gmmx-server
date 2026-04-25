package com.gmmx.mvp.service;

import com.gmmx.mvp.dto.DashboardDtos;
import com.gmmx.mvp.entity.UserRole;
import com.gmmx.mvp.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserAccountRepository userAccountRepository;

    public DashboardDtos.OwnerStatsResponse getOwnerStats() {
        // Real counts from DB
        long totalMembers = userAccountRepository.countByRole(UserRole.MEMBER);
        long activeTrainers = userAccountRepository.countByRole(UserRole.TRAINER);
        
        // Mocking revenue for now as payment implementation might be partial
        // but we can calculate it if payments table is populated.
        String monthlyRevenue = "₹0.0"; 
        String newMembersThisMonth = "+0";

        return DashboardDtos.OwnerStatsResponse.builder()
                .totalMembers(String.format("%,d", totalMembers))
                .activeTrainers(String.valueOf(activeTrainers))
                .monthlyRevenue(monthlyRevenue)
                .newMembersThisMonth(newMembersThisMonth)
                .build();
    }

    public List<DashboardDtos.RecentActivityResponse> getRecentActivity() {
        List<DashboardDtos.RecentActivityResponse> activities = new ArrayList<>();
        
        // Fetch latest users
        // This is a simplified recent activity for MVP
        activities.add(DashboardDtos.RecentActivityResponse.builder()
                .title("System Ready")
                .subtitle("Real-time dashboard connected")
                .icon("info")
                .time("Just now")
                .build());

        return activities;
    }
}
