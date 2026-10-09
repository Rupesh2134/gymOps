package com.gymops.service.impl;

import com.gymops.dto.payment.PaymentCreateRequest;
import com.gymops.dto.payment.PaymentResponse;
import com.gymops.dto.payment.PaymentUpdateRequest;
import com.gymops.entity.Member;
import com.gymops.entity.Membership;
import com.gymops.entity.Payment;
import com.gymops.entity.PaymentStatus;
import com.gymops.exception.BusinessRuleException;
import com.gymops.exception.DuplicateResourceException;
import com.gymops.exception.ResourceNotFoundException;
import com.gymops.repository.MemberRepository;
import com.gymops.repository.MembershipRepository;
import com.gymops.repository.PaymentRepository;
import com.gymops.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                             MemberRepository memberRepository,
                             MembershipRepository membershipRepository) {
        this.paymentRepository = paymentRepository;
        this.memberRepository = memberRepository;
        this.membershipRepository = membershipRepository;
    }

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentCreateRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Payment request must not be null");
        }

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + request.getMemberId()));

        Membership membership = membershipRepository.findById(request.getMembershipId())
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found with id: " + request.getMembershipId()));

        if (!membership.getMember().getId().equals(member.getId())) {
            throw new BusinessRuleException("Payment membership does not belong to the selected member");
        }

        if (request.getTransactionReference() != null && paymentRepository.existsByTransactionReference(request.getTransactionReference().trim())) {
            throw new DuplicateResourceException("Transaction reference already exists: " + request.getTransactionReference().trim());
        }

        if (request.getAmount() == null || request.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Payment amount must be greater than zero");
        }

        Payment payment = request.toEntity(member, membership);
        Payment savedPayment = paymentRepository.save(payment);
        return PaymentResponse.fromEntity(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return PaymentResponse.fromEntity(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(PaymentResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public PaymentResponse updatePayment(Long id, PaymentUpdateRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Payment update request must not be null");
        }

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        if (request.getMemberId() != null) {
            Member member = memberRepository.findById(request.getMemberId())
                    .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + request.getMemberId()));
            payment.setMember(member);
        }

        if (request.getMembershipId() != null) {
            Membership membership = membershipRepository.findById(request.getMembershipId())
                    .orElseThrow(() -> new ResourceNotFoundException("Membership not found with id: " + request.getMembershipId()));
            if (payment.getMember() != null && !membership.getMember().getId().equals(payment.getMember().getId())) {
                throw new BusinessRuleException("Payment membership does not belong to the selected member");
            }
            payment.setMembership(membership);
        }

        if (request.getAmount() != null) {
            if (request.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new BusinessRuleException("Payment amount must be greater than zero");
            }
            payment.setAmount(request.getAmount());
        }

        if (request.getPaymentDate() != null) {
            payment.setPaymentDate(request.getPaymentDate());
        }

        if (request.getPaymentMethod() != null) {
            payment.setPaymentMethod(request.getPaymentMethod());
        }

        if (request.getPaymentStatus() != null) {
            payment.setPaymentStatus(request.getPaymentStatus());
        }

        if (request.getTransactionReference() != null) {
            String newReference = request.getTransactionReference().trim();
            if (!newReference.equals(payment.getTransactionReference()) && paymentRepository.existsByTransactionReference(newReference)) {
                throw new DuplicateResourceException("Transaction reference already exists: " + newReference);
            }
            payment.setTransactionReference(newReference);
        }

        Payment updatedPayment = paymentRepository.save(payment);
        return PaymentResponse.fromEntity(updatedPayment);
    }

    @Override
    @Transactional
    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        if (payment.getPaymentStatus() == PaymentStatus.REFUNDED) {
            throw new BusinessRuleException("Payment is already refunded and cannot be deleted again");
        }

        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);
    }
}
