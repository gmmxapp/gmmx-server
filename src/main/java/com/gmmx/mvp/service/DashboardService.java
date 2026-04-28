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
    private final com.gmmx.mvp.repository.MemberProfileRepository memberProfileRepository;

    public DashboardDtos.OwnerStatsResponse getOwnerStats() {
        long totalMembers = userAccountRepository.countByRole(UserRole.MEMBER);
        long activeTrainers = userAccountRepository.countByRole(UserRole.TRAINER);
        
        java.time.LocalDate now = java.time.LocalDate.now();
        java.time.LocalDate monthStart = now.withDayOfMonth(1);
        
        // Real Revenue from Member Profiles
        java.math.BigDecimal monthlyRevenueVal = memberProfileRepository.findAll().stream()
                .filter(m -> m.getJoinDate() != null && !m.getJoinDate().isBefore(monthStart))
                .map(m -> m.getFeesPaid() != null ? m.getFeesPaid() : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        
        long newMembersThisMonthCount = memberProfileRepository.findAll().stream()
                .filter(m -> m.getJoinDate() != null && !m.getJoinDate().isBefore(monthStart))
                .count();

        // Calculate Weekly Revenue
        List<DashboardDtos.DailyRevenue> weeklyRevenue = new ArrayList<>();
        java.math.BigDecimal totalWeekly = java.math.BigDecimal.ZERO;
        
        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate date = now.minusDays(i);
            String dayName = date.getDayOfWeek().name().substring(0, 3);
            
            java.math.BigDecimal dailyAmount = memberProfileRepository.findAll().stream()
                .filter(m -> m.getJoinDate() != null && m.getJoinDate().equals(date))
                .map(m -> m.getFeesPaid() != null ? m.getFeesPaid() : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            
            weeklyRevenue.add(DashboardDtos.DailyRevenue.builder()
                .day(dayName)
                .amount(dailyAmount.doubleValue())
                .build());
            
            totalWeekly = totalWeekly.add(dailyAmount);
        }

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        
        return DashboardDtos.OwnerStatsResponse.builder()
                .totalMembers(String.format("%,d", totalMembers))
                .activeTrainers(String.valueOf(activeTrainers))
                .monthlyRevenue(format.format(monthlyRevenueVal))
                .newMembersThisMonth("+" + newMembersThisMonthCount)
                .weeklyRevenue(weeklyRevenue)
                .totalWeeklyRevenue(format.format(totalWeekly))
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
