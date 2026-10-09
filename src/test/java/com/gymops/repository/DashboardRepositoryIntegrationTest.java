package com.gymops.repository;

import com.gymops.dto.dashboard.DashboardSummaryResponse;
import com.gymops.entity.Attendance;
import com.gymops.entity.Member;
import com.gymops.entity.MemberStatus;
import com.gymops.entity.Membership;
import com.gymops.entity.MembershipPlan;
import com.gymops.entity.MembershipStatus;
import com.gymops.entity.Payment;
import com.gymops.entity.PaymentMethod;
import com.gymops.entity.PaymentStatus;
import com.gymops.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:gymops-dashboard;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class DashboardRepositoryIntegrationTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 9);

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private MembershipPlanRepository membershipPlanRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private DashboardServiceImpl dashboardService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-10-09T12:00:00Z"), ZoneOffset.UTC);
        dashboardService = new DashboardServiceImpl(
                memberRepository,
                membershipRepository,
                attendanceRepository,
                paymentRepository,
                clock);
    }

    @Test
    void summaryReturnsZerosForEmptyDatabase() {
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
    void summaryAggregatesStatusesAndInclusiveDateBoundariesFromDatabase() {
        Member activeMember = saveMember("Asha", "1001", MemberStatus.ACTIVE);
        Member inactiveMember = saveMember("Ravi", "1002", MemberStatus.INACTIVE);

        MembershipPlan oneMonthPlan = savePlan("One month", 1);
        Membership endToday = saveMembership(activeMember, oneMonthPlan, LocalDate.of(2026, 9, 9),
                MembershipStatus.ACTIVE);
        saveMembership(activeMember, oneMonthPlan, LocalDate.of(2026, 9, 16), MembershipStatus.ACTIVE);
        saveMembership(activeMember, oneMonthPlan, LocalDate.of(2026, 9, 17), MembershipStatus.ACTIVE);
        saveMembership(inactiveMember, oneMonthPlan, LocalDate.of(2026, 9, 9), MembershipStatus.EXPIRED);

        saveAttendance(activeMember, TODAY);
        saveAttendance(inactiveMember, TODAY.minusDays(1));

        savePayment(activeMember, endToday, "25.50", LocalDateTime.of(2026, 10, 1, 0, 0),
                PaymentStatus.PAID, "paid-current-month");
        savePayment(activeMember, endToday, "10.00", LocalDateTime.of(2026, 11, 1, 0, 0),
                PaymentStatus.PAID, "paid-next-month");
        savePayment(activeMember, endToday, "90.00", LocalDateTime.of(2026, 10, 15, 12, 0),
                PaymentStatus.PENDING, "pending-current-month");
        savePayment(activeMember, endToday, "40.00", LocalDateTime.of(2026, 10, 15, 12, 0),
                PaymentStatus.REFUNDED, "refunded-current-month");
        savePayment(activeMember, endToday, "15.00", LocalDateTime.of(2026, 10, 15, 12, 0),
                PaymentStatus.FAILED, "failed-current-month");

        DashboardSummaryResponse summary = dashboardService.getSummary();

        assertEquals(2L, summary.getTotalMembers());
        assertEquals(1L, summary.getActiveMembers());
        assertEquals(1L, summary.getInactiveMembers());
        assertEquals(3L, summary.getActiveMemberships());
        assertEquals(1L, summary.getExpiredMemberships());
        assertEquals(2L, summary.getExpiringMemberships());
        assertEquals(1L, summary.getTodayAttendance());
        assertEquals(new BigDecimal("25.50"), summary.getMonthlyRevenue());
        assertEquals(1L, summary.getPendingPayments());
    }

    private Member saveMember(String firstName, String phone, MemberStatus status) {
        Member member = new Member();
        member.setFirstName(firstName);
        member.setPhone(phone);
        member.setJoinDate(TODAY.minusMonths(1));
        member.setStatus(status);
        return memberRepository.saveAndFlush(member);
    }

    private MembershipPlan savePlan(String name, int durationInMonths) {
        MembershipPlan plan = new MembershipPlan();
        plan.setName(name);
        plan.setDurationInMonths(durationInMonths);
        plan.setPrice(new BigDecimal("29.99"));
        plan.setActive(true);
        return membershipPlanRepository.saveAndFlush(plan);
    }

    private Membership saveMembership(
            Member member,
            MembershipPlan plan,
            LocalDate startDate,
            MembershipStatus status) {
        Membership membership = new Membership();
        membership.setMember(member);
        membership.setMembershipPlan(plan);
        membership.setStartDate(startDate);
        membership.setStatus(status);
        return membershipRepository.saveAndFlush(membership);
    }

    private void saveAttendance(Member member, LocalDate date) {
        Attendance attendance = new Attendance();
        attendance.setMember(member);
        attendance.setCheckInTime(date.atTime(9, 0));
        attendance.setAttendanceDate(date);
        attendanceRepository.saveAndFlush(attendance);
    }

    private void savePayment(
            Member member,
            Membership membership,
            String amount,
            LocalDateTime paymentDate,
            PaymentStatus status,
            String transactionReference) {
        Payment payment = new Payment();
        payment.setMember(member);
        payment.setMembership(membership);
        payment.setAmount(new BigDecimal(amount));
        payment.setPaymentDate(paymentDate);
        payment.setPaymentMethod(PaymentMethod.CASH);
        payment.setPaymentStatus(status);
        payment.setTransactionReference(transactionReference);
        paymentRepository.saveAndFlush(payment);
    }
}
