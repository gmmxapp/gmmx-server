package com.gmmx.mvp.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

public class DashboardDtos {

    @Data
    @Builder
    public static class OwnerStatsResponse {
        private String totalMembers;
        private String activeTrainers;
        private String monthlyRevenue;
        private String newMembersThisMonth;
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
