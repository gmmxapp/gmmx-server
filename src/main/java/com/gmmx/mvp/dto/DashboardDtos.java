package com.gmmx.mvp.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardDtos {

    @Data
    @Builder
    public static class OwnerDashboardStats {
        private String totalMembers;
        private String activeMembers;
        private String activeTrainers;
        private String newLeadsToday;
        private String totalLeads;
        private String monthlyRevenue;
        private String monthlyExpenses;
        private List<MonthlyData> revenueTrend;
        private List<DailyRevenue> weeklyRevenue; // Match Flutter
        private String totalWeeklyRevenue; // Match Flutter
        private Map<String, Long> membershipDistribution;
        private long attendanceToday;
        private String newMembersThisMonth; // Match Flutter
    }

    @Data
    @Builder
    public static class MonthlyData {
        private String month;
        private BigDecimal value;
    }

    @Data
    @Builder
    public static class OwnerStatsResponse {
        private String totalMembers;
        private String activeTrainers;
        private String monthlyRevenue;
        private String newMembersThisMonth;
        private List<DailyRevenue> weeklyRevenue;
        private String totalWeeklyRevenue;
    }

    @Data
    @Builder
    public static class DailyRevenue {
        private String day;
        private double amount;
    }

    @Data
    @Builder
    public static class RecentActivityResponse {
        private String title;
        private String subtitle;
        private String icon;
        private String time;
    }
}
