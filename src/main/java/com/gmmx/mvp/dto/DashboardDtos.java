package com.gmmx.mvp.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardDtos {

    @Data
    @Builder
    public static class OwnerDashboardStats {
        private long totalMembers;
        private long activeMembers;
        private long newLeadsToday;
        private long totalLeads;
        private BigDecimal monthlyRevenue;
        private BigDecimal monthlyExpenses;
        private List<MonthlyData> revenueTrend;
        private Map<String, Long> membershipDistribution;
        private long attendanceToday;
    }

    @Data
    @Builder
    public static class MonthlyData {
        private String month;
        private BigDecimal value;
    }
}
