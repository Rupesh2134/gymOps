package com.gymops.dto.payment;

import com.gymops.entity.Member;
import com.gymops.entity.Membership;
import com.gymops.entity.Payment;
import com.gymops.entity.PaymentMethod;
import com.gymops.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Member, membership, amount, date, and method for a new payment.")
public class PaymentCreateRequest {

    @Schema(description = "ID of the paying member.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Member id is required")
    private Long memberId;

    @Schema(description = "ID of the associated membership.", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Membership id is required")
    private Long membershipId;

    @Schema(description = "Payment amount greater than zero.", example = "49.99",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @Schema(description = "Payment timestamp in ISO-8601 local date-time format.",
            example = "2026-10-09T12:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Payment date is required")
    private LocalDateTime paymentDate;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    private String transactionReference;

    public PaymentCreateRequest() {
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(Long membershipId) {
        this.membershipId = membershipId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public Payment toEntity(Member member, Membership membership) {
        Payment payment = new Payment();
        payment.setMember(member);
        payment.setMembership(membership);
        payment.setAmount(this.amount);
        payment.setPaymentDate(this.paymentDate);
        payment.setPaymentMethod(this.paymentMethod);
        payment.setPaymentStatus(this.paymentStatus != null ? this.paymentStatus : PaymentStatus.PENDING);
        payment.setTransactionReference(this.transactionReference);
        return payment;
    }
}
