package com.gymops.repository;

import com.gymops.entity.Member;
import com.gymops.entity.Membership;
import com.gymops.entity.Payment;
import com.gymops.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByMember(Member member);

    List<Payment> findByMembership(Membership membership);

    List<Payment> findByPaymentStatus(PaymentStatus status);

    long countByPaymentStatus(PaymentStatus status);

    @Query("""
            SELECT COALESCE(SUM(payment.amount), 0)
            FROM Payment payment
            WHERE payment.paymentStatus = :status
              AND payment.paymentDate >= :startDate
              AND payment.paymentDate < :endDate
            """)
    BigDecimal sumAmountByStatusAndPaymentDateRange(
            @Param("status") PaymentStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    boolean existsByTransactionReference(String transactionReference);
}
