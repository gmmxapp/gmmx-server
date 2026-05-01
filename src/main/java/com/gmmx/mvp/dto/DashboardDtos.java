package com.gmmx.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @AllArgsConstructor
    @NoArgsConstructor
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
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClientStatsResponse {
        private String planName;
        private String expiryDate;
        private int totalVisits;
        private int calories;
        private List<ExerciseResponse> todayWorkout;
        private String trainerId; // Added
        private String trainerName;
        private String trainerSpecialty;
        private List<AttendanceDayResponse> attendanceStreak;
        private int steps;
        private int stepGoal;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExerciseResponse {
        private String name;
        private String sets;
        private String icon;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AttendanceDayResponse {
        private String day;
        private boolean present;
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
