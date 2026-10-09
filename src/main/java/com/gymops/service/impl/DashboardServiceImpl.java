package com.gymops.service.impl;

import com.gymops.dto.dashboard.DashboardSummaryResponse;
import com.gymops.entity.MemberStatus;
import com.gymops.entity.MembershipStatus;
import com.gymops.entity.PaymentStatus;
import com.gymops.repository.AttendanceRepository;
import com.gymops.repository.MemberRepository;
import com.gymops.repository.MembershipRepository;
import com.gymops.repository.PaymentRepository;
import com.gymops.service.DashboardService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;
    private final AttendanceRepository attendanceRepository;
    private final PaymentRepository paymentRepository;
    private final Clock clock;

    public DashboardServiceImpl(
            MemberRepository memberRepository,
            MembershipRepository membershipRepository,
            AttendanceRepository attendanceRepository,
            PaymentRepository paymentRepository,
            Clock clock) {
        this.memberRepository = memberRepository;
        this.membershipRepository = membershipRepository;
        this.attendanceRepository = attendanceRepository;
        this.paymentRepository = paymentRepository;
        this.clock = clock;
    }

    @Override
    public DashboardSummaryResponse getSummary() {
        LocalDate today = LocalDate.now(clock);
        YearMonth currentMonth = YearMonth.from(today);
        LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime nextMonthStart = currentMonth.plusMonths(1).atDay(1).atStartOfDay();

        return new DashboardSummaryResponse(
                memberRepository.count(),
                memberRepository.countByStatus(MemberStatus.ACTIVE),
                memberRepository.countByStatus(MemberStatus.INACTIVE),
                membershipRepository.countByStatus(MembershipStatus.ACTIVE),
                membershipRepository.countByStatus(MembershipStatus.EXPIRED),
                membershipRepository.countByStatusAndEndDateBetween(
                        MembershipStatus.ACTIVE, today, today.plusDays(7)),
                attendanceRepository.countByAttendanceDate(today),
                zeroIfNull(paymentRepository.sumAmountByStatusAndPaymentDateRange(
                        PaymentStatus.PAID, monthStart, nextMonthStart)),
                paymentRepository.countByPaymentStatus(PaymentStatus.PENDING));
    }

    private BigDecimal zeroIfNull(BigDecimal amount) {
        return amount != null ? amount : BigDecimal.ZERO;
    }
}
