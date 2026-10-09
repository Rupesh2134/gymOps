package com.gymops.service;

import com.gymops.dto.payment.PaymentCreateRequest;
import com.gymops.dto.payment.PaymentResponse;
import com.gymops.dto.payment.PaymentUpdateRequest;
import com.gymops.entity.Member;
import com.gymops.entity.MemberStatus;
import com.gymops.entity.Membership;
import com.gymops.entity.MembershipStatus;
import com.gymops.entity.Payment;
import com.gymops.entity.PaymentMethod;
import com.gymops.entity.PaymentStatus;
import com.gymops.exception.BusinessRuleException;
import com.gymops.exception.ResourceNotFoundException;
import com.gymops.repository.MemberRepository;
import com.gymops.repository.MembershipRepository;
import com.gymops.repository.PaymentRepository;
import com.gymops.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(paymentRepository, memberRepository, membershipRepository);
    }

    @Test
    void createPayment_shouldSaveAndReturnResponse() {
        Member member = new Member();
        setMemberId(member, 1L);
        member.setStatus(MemberStatus.ACTIVE);

        Membership membership = new Membership();
        setMembershipId(membership, 11L);
        membership.setMember(member);
        membership.setStatus(MembershipStatus.ACTIVE);

        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setMemberId(1L);
        request.setMembershipId(11L);
        request.setAmount(new BigDecimal("1500.00"));
        request.setPaymentDate(LocalDateTime.now());
        request.setPaymentMethod(PaymentMethod.CARD);
        request.setTransactionReference("txn-001");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(membershipRepository.findById(11L)).thenReturn(Optional.of(membership));
        when(paymentRepository.existsByTransactionReference("txn-001")).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment saved = invocation.getArgument(0);
            setPaymentId(saved, 99L);
            return saved;
        });

        PaymentResponse response = paymentService.createPayment(request);

        assertNotNull(response);
        assertEquals(99L, response.getId());
        assertEquals(new BigDecimal("1500.00"), response.getAmount());
    }

    @Test
    void createPayment_shouldRejectDuplicateTransactionReference() {
        Member member = new Member();
        setMemberId(member, 1L);
        member.setStatus(MemberStatus.ACTIVE);

        Membership membership = new Membership();
        setMembershipId(membership, 11L);
        membership.setMember(member);
        membership.setStatus(MembershipStatus.ACTIVE);

        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setMemberId(1L);
        request.setMembershipId(11L);
        request.setAmount(new BigDecimal("1500.00"));
        request.setPaymentDate(LocalDateTime.now());
        request.setPaymentMethod(PaymentMethod.UPI);
        request.setTransactionReference("txn-dup");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(membershipRepository.findById(11L)).thenReturn(Optional.of(membership));
        when(paymentRepository.existsByTransactionReference("txn-dup")).thenReturn(true);

        assertThrows(com.gymops.exception.DuplicateResourceException.class, () -> paymentService.createPayment(request));
    }

    @Test
    void getPaymentById_shouldReturnPayment() {
        Payment payment = new Payment();
        setPaymentId(payment, 7L);
        payment.setMember(new Member());
        setMemberId(payment.getMember(), 9L);
        payment.setMembership(new Membership());
        setMembershipId(payment.getMembership(), 21L);
        payment.setAmount(new BigDecimal("2000.00"));
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(PaymentMethod.CARD);
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setTransactionReference("txn-777");

        when(paymentRepository.findById(7L)).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPaymentById(7L);

        assertEquals(7L, response.getId());
        assertEquals("txn-777", response.getTransactionReference());
    }

    @Test
    void updatePayment_shouldUpdateStatusAndAmount() {
        Payment payment = new Payment();
        setPaymentId(payment, 8L);
        payment.setMember(new Member());
        setMemberId(payment.getMember(), 10L);
        payment.setMembership(new Membership());
        setMembershipId(payment.getMembership(), 22L);
        payment.setAmount(new BigDecimal("1000.00"));
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(PaymentMethod.CASH);
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setTransactionReference("txn-888");

        PaymentUpdateRequest request = new PaymentUpdateRequest();
        request.setPaymentStatus(PaymentStatus.REFUNDED);

        when(paymentRepository.findById(8L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.updatePayment(8L, request);

        assertEquals(PaymentStatus.REFUNDED, response.getPaymentStatus());
    }

    @Test
    void deletePayment_shouldSetRefundedStatus() {
        Payment payment = new Payment();
        setPaymentId(payment, 9L);
        payment.setMember(new Member());
        setMemberId(payment.getMember(), 11L);
        payment.setMembership(new Membership());
        setMembershipId(payment.getMembership(), 23L);
        payment.setAmount(new BigDecimal("500.00"));
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(PaymentMethod.CARD);
        payment.setPaymentStatus(PaymentStatus.PAID);

        when(paymentRepository.findById(9L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        paymentService.deletePayment(9L);

        assertEquals(PaymentStatus.REFUNDED, payment.getPaymentStatus());
        verify(paymentRepository).save(payment);
    }

    @Test
    void getAllPayments_shouldReturnList() {
        Payment payment = new Payment();
        setPaymentId(payment, 10L);
        payment.setMember(new Member());
        setMemberId(payment.getMember(), 12L);
        payment.setMembership(new Membership());
        setMembershipId(payment.getMembership(), 24L);
        payment.setAmount(new BigDecimal("600.00"));
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(PaymentMethod.CARD);
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setTransactionReference("txn-10");

        when(paymentRepository.findAll()).thenReturn(List.of(payment));

        List<PaymentResponse> result = paymentService.getAllPayments();

        assertEquals(1, result.size());
        assertEquals("txn-10", result.get(0).getTransactionReference());
    }

    @Test
    void getPaymentById_shouldThrowWhenMissing() {
        when(paymentRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPaymentById(404L));
    }

    private void setMemberId(Member member, long id) {
        try {
            Field field = Member.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(member, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void setMembershipId(Membership membership, long id) {
        try {
            Field field = Membership.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(membership, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void setPaymentId(Payment payment, long id) {
        try {
            Field field = Payment.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(payment, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
