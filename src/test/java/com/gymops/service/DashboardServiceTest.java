package com.gymops.service;

import com.gymops.dto.dashboard.DashboardSummaryResponse;
import com.gymops.entity.MemberStatus;
import com.gymops.entity.MembershipStatus;
import com.gymops.entity.PaymentStatus;
import com.gymops.repository.AttendanceRepository;
import com.gymops.repository.MemberRepository;
import com.gymops.repository.MembershipRepository;
import com.gymops.repository.PaymentRepository;
import com.gymops.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 9);

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private PaymentRepository paymentRepository;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-10-09T12:00:00Z"), ZoneOffset.UTC);
        dashboardService = new DashboardServiceImpl(
                memberRepository, membershipRepository, attendanceRepository, paymentRepository, clock);
    }

    @Test
    void getSummary_returnsZeroMetricsForEmptyDatabase() {
        when(memberRepository.count()).thenReturn(0L);
        when(memberRepository.countByStatus(MemberStatus.ACTIVE)).thenReturn(0L);
        when(memberRepository.countByStatus(MemberStatus.INACTIVE)).thenReturn(0L);
        when(membershipRepository.countByStatus(MembershipStatus.ACTIVE)).thenReturn(0L);
        when(membershipRepository.countByStatus(MembershipStatus.EXPIRED)).thenReturn(0L);
        when(membershipRepository.countByStatusAndEndDateBetween(
                MembershipStatus.ACTIVE, TODAY, TODAY.plusDays(7))).thenReturn(0L);
        when(attendanceRepository.countByAttendanceDate(TODAY)).thenReturn(0L);
        when(paymentRepository.sumAmountByStatusAndPaymentDateRange(
                PaymentStatus.PAID,
                LocalDateTime.of(2026, 10, 1, 0, 0),
                LocalDateTime.of(2026, 11, 1, 0, 0))).thenReturn(null);
        when(paymentRepository.countByPaymentStatus(PaymentStatus.PENDING)).thenReturn(0L);

        DashboardSummaryResponse summary = dashboardService.getSummary();

        assertEquals(0L, summary.getTotalMembers());
        assertEquals(0L, summary.getActiveMembers());
        assertEquals(0L, summary.getInactiveMembers());
        assertEquals(0L, summary.getActiveMemberships());
        assertEquals(0L, summary.getExpiredMemberships());
        assertEquals(0L, summary.getExpiringMemberships());
        assertEquals(0L, summary.getTodayAttendance());
        assertEquals(BigDecimal.ZERO, summary.getMonthlyRevenue());
        assertEquals(0L, summary.getPendingPayments());
    }

    @Test
    void getSummary_includesMemberMembershipAttendanceAndPendingPaymentCounts() {
        when(memberRepository.count()).thenReturn(10L);
        when(memberRepository.countByStatus(MemberStatus.ACTIVE)).thenReturn(7L);
        when(memberRepository.countByStatus(MemberStatus.INACTIVE)).thenReturn(3L);
        when(membershipRepository.countByStatus(MembershipStatus.ACTIVE)).thenReturn(5L);
        when(membershipRepository.countByStatus(MembershipStatus.EXPIRED)).thenReturn(4L);
        when(membershipRepository.countByStatusAndEndDateBetween(
                MembershipStatus.ACTIVE, TODAY, TODAY.plusDays(7))).thenReturn(2L);
        when(attendanceRepository.countByAttendanceDate(TODAY)).thenReturn(6L);
        when(paymentRepository.sumAmountByStatusAndPaymentDateRange(
                PaymentStatus.PAID,
                LocalDateTime.of(2026, 10, 1, 0, 0),
                LocalDateTime.of(2026, 11, 1, 0, 0))).thenReturn(new BigDecimal("125.50"));
        when(paymentRepository.countByPaymentStatus(PaymentStatus.PENDING)).thenReturn(3L);

        DashboardSummaryResponse summary = dashboardService.getSummary();

        assertEquals(10L, summary.getTotalMembers());
        assertEquals(7L, summary.getActiveMembers());
        assertEquals(3L, summary.getInactiveMembers());
        assertEquals(5L, summary.getActiveMemberships());
        assertEquals(4L, summary.getExpiredMemberships());
        assertEquals(2L, summary.getExpiringMemberships());
        assertEquals(6L, summary.getTodayAttendance());
        assertEquals(new BigDecimal("125.50"), summary.getMonthlyRevenue());
        assertEquals(3L, summary.getPendingPayments());
    }

    @Test
    void getSummary_countsExpiringMembershipsFromTodayThroughSeventhDayInclusive() {
        when(memberRepository.count()).thenReturn(0L);
        when(memberRepository.countByStatus(MemberStatus.ACTIVE)).thenReturn(0L);
        when(memberRepository.countByStatus(MemberStatus.INACTIVE)).thenReturn(0L);
        when(membershipRepository.countByStatus(MembershipStatus.ACTIVE)).thenReturn(0L);
        when(membershipRepository.countByStatus(MembershipStatus.EXPIRED)).thenReturn(0L);
        when(membershipRepository.countByStatusAndEndDateBetween(
                MembershipStatus.ACTIVE, TODAY, TODAY.plusDays(7))).thenReturn(2L);
        when(attendanceRepository.countByAttendanceDate(TODAY)).thenReturn(0L);
        when(paymentRepository.sumAmountByStatusAndPaymentDateRange(
                PaymentStatus.PAID,
                LocalDateTime.of(2026, 10, 1, 0, 0),
                LocalDateTime.of(2026, 11, 1, 0, 0))).thenReturn(BigDecimal.ZERO);
        when(paymentRepository.countByPaymentStatus(PaymentStatus.PENDING)).thenReturn(0L);

        DashboardSummaryResponse summary = dashboardService.getSummary();

        assertEquals(2L, summary.getExpiringMemberships());
        verify(membershipRepository).countByStatusAndEndDateBetween(
                MembershipStatus.ACTIVE, TODAY, LocalDate.of(2026, 10, 16));
    }

    @Test
    void getSummary_usesCurrentCalendarMonthForPaidRevenue() {
        when(memberRepository.count()).thenReturn(0L);
        when(memberRepository.countByStatus(MemberStatus.ACTIVE)).thenReturn(0L);
        when(memberRepository.countByStatus(MemberStatus.INACTIVE)).thenReturn(0L);
        when(membershipRepository.countByStatus(MembershipStatus.ACTIVE)).thenReturn(0L);
        when(membershipRepository.countByStatus(MembershipStatus.EXPIRED)).thenReturn(0L);
        when(membershipRepository.countByStatusAndEndDateBetween(
                MembershipStatus.ACTIVE, TODAY, TODAY.plusDays(7))).thenReturn(0L);
        when(attendanceRepository.countByAttendanceDate(TODAY)).thenReturn(0L);
        when(paymentRepository.sumAmountByStatusAndPaymentDateRange(
                PaymentStatus.PAID,
                LocalDateTime.of(2026, 10, 1, 0, 0),
                LocalDateTime.of(2026, 11, 1, 0, 0))).thenReturn(BigDecimal.ZERO);
        when(paymentRepository.countByPaymentStatus(PaymentStatus.PENDING)).thenReturn(0L);

        dashboardService.getSummary();

        verify(paymentRepository).sumAmountByStatusAndPaymentDateRange(
                PaymentStatus.PAID,
                LocalDateTime.of(2026, 10, 1, 0, 0),
                LocalDateTime.of(2026, 11, 1, 0, 0));
    }
}
