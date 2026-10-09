package com.gymops.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Aggregate member, membership, attendance, and payment metrics.")
public class DashboardSummaryResponse {

    @Schema(description = "Total number of members.")
    private final long totalMembers;
    @Schema(description = "Number of active members.")
    private final long activeMembers;
    @Schema(description = "Number of inactive members.")
    private final long inactiveMembers;
    @Schema(description = "Number of active memberships.")
    private final long activeMemberships;
    @Schema(description = "Number of expired memberships.")
    private final long expiredMemberships;
    /**
     * Active memberships ending from today through seven days from today, inclusive.
     */
    @Schema(description = "Active memberships ending today through seven days from today, inclusive.")
    private final long expiringMemberships;
    @Schema(description = "Attendance records for today.")
    private final long todayAttendance;
    @Schema(description = "Sum of PAID payments in the current calendar month.", example = "125.50")
    private final BigDecimal monthlyRevenue;
    @Schema(description = "Number of pending payments.")
    private final long pendingPayments;

    public DashboardSummaryResponse(
            long totalMembers,
            long activeMembers,
            long inactiveMembers,
            long activeMemberships,
            long expiredMemberships,
            long expiringMemberships,
            long todayAttendance,
            BigDecimal monthlyRevenue,
            long pendingPayments) {
        this.totalMembers = totalMembers;
        this.activeMembers = activeMembers;
        this.inactiveMembers = inactiveMembers;
        this.activeMemberships = activeMemberships;
        this.expiredMemberships = expiredMemberships;
        this.expiringMemberships = expiringMemberships;
        this.todayAttendance = todayAttendance;
        this.monthlyRevenue = monthlyRevenue;
        this.pendingPayments = pendingPayments;
    }

    public long getTotalMembers() {
        return totalMembers;
    }

    public long getActiveMembers() {
        return activeMembers;
    }

    public long getInactiveMembers() {
        return inactiveMembers;
    }

    public long getActiveMemberships() {
        return activeMemberships;
    }

    public long getExpiredMemberships() {
        return expiredMemberships;
    }

    public long getExpiringMemberships() {
        return expiringMemberships;
    }

    public long getTodayAttendance() {
        return todayAttendance;
    }

    public BigDecimal getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public long getPendingPayments() {
        return pendingPayments;
    }
}
