package com.gmmx.mvp.dto;

import lombok.Builder;
import lombok.Data;

public class DashboardDtos {

    @Data
    @Builder
    public static class OwnerStatsResponse {
        private String totalMembers;
        private String activeTrainers;
        private String monthlyRevenue;
        private String newMembersThisMonth;
        private java.util.List<DailyRevenue> weeklyRevenue;
        private String totalWeeklyRevenue;
    }

    @Data
    @Builder
    public static class DailyRevenue {
        private String day; // Mon, Tue, etc.
        private double amount;
    }

    @Data
    @Builder
    public static class RecentActivityResponse {
        private String title;
        private String subtitle;
        private String icon; // people, payment, fitness, etc.
        private String time;
    }
}
