package com.gmmx.mvp.service;

import com.gmmx.mvp.core.tenant.TenantContext;
import com.gmmx.mvp.dto.DashboardDtos;
import com.gmmx.mvp.entity.UserAccount;
import com.gmmx.mvp.entity.UserRole;
import com.gmmx.mvp.repository.MemberProfileRepository;
import com.gmmx.mvp.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserAccountRepository userAccountRepository;
    private final MemberProfileRepository memberProfileRepository;

    public DashboardDtos.OwnerStatsResponse getOwnerStats() {
        UUID tenantId = TenantContext.getTenantId();

        // Tenant-scoped counts
        long totalMembers = userAccountRepository.countByRoleAndTenantId(UserRole.MEMBER, tenantId);
        long activeTrainers = userAccountRepository.countByRoleAndTenantId(UserRole.TRAINER, tenantId);

        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);

        // Real Revenue: sum of feesPaid for members who joined this month (in this tenant)
        BigDecimal monthlyRevenueVal = memberProfileRepository.findAll().stream()
                .filter(m -> m.getTenantId() != null && m.getTenantId().equals(tenantId))
                .filter(m -> m.getJoinDate() != null && !m.getJoinDate().isBefore(monthStart))
                .map(m -> m.getFeesPaid() != null ? m.getFeesPaid() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long newMembersThisMonthCount = memberProfileRepository.findAll().stream()
                .filter(m -> m.getTenantId() != null && m.getTenantId().equals(tenantId))
                .filter(m -> m.getJoinDate() != null && !m.getJoinDate().isBefore(monthStart))
                .count();

        // Weekly Revenue (last 7 days)
        List<DashboardDtos.DailyRevenue> weeklyRevenue = new ArrayList<>();
        BigDecimal totalWeekly = BigDecimal.ZERO;

        for (int i = 6; i >= 0; i--) {
            LocalDate date = now.minusDays(i);
            String dayName = date.getDayOfWeek().name().substring(0, 3);

            BigDecimal dailyAmount = memberProfileRepository.findAll().stream()
                .filter(m -> m.getTenantId() != null && m.getTenantId().equals(tenantId))
                .filter(m -> m.getJoinDate() != null && m.getJoinDate().equals(date))
                .map(m -> m.getFeesPaid() != null ? m.getFeesPaid() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

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
        UUID tenantId = TenantContext.getTenantId();
        List<DashboardDtos.RecentActivityResponse> activities = new ArrayList<>();

        // Fetch latest 10 users across all roles (members, trainers added recently)
        List<UserAccount> recentUsers = userAccountRepository.findTop10ByTenantIdOrderByCreatedAtDesc(tenantId);

        for (UserAccount user : recentUsers) {
            String icon;
            String title;
            String subtitle;

            if (user.getRole() == UserRole.MEMBER) {
                icon = "person_add";
                title = "New Member Joined";
                subtitle = user.getFullName();
            } else if (user.getRole() == UserRole.TRAINER) {
                icon = "fitness_center";
                title = "Trainer Added";
                subtitle = user.getFullName() + " joined the team";
            } else {
                continue;
            }

            String timeAgo = formatTimeAgo(user.getCreatedAt());
            activities.add(DashboardDtos.RecentActivityResponse.builder()
                    .title(title)
                    .subtitle(subtitle)
                    .icon(icon)
                    .time(timeAgo)
                    .build());
        }

        if (activities.isEmpty()) {
            activities.add(DashboardDtos.RecentActivityResponse.builder()
                    .title("Welcome to GMMX!")
                    .subtitle("Add members and trainers to see activity here")
                    .icon("info")
                    .time("Just now")
                    .build());
        }

        return activities;
    }

    private String formatTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "Recently";
        Duration duration = Duration.between(dateTime, LocalDateTime.now());
        if (duration.toMinutes() < 1) return "Just now";
        if (duration.toMinutes() < 60) return duration.toMinutes() + "m ago";
        if (duration.toHours() < 24) return duration.toHours() + "h ago";
        return duration.toDays() + "d ago";
    }
}
