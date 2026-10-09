package com.gymops.entity;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void requiresPositiveAmountAndPaymentDetailsButAllowsNullTransactionReference() {
        Payment payment = new Payment();

        assertTrue(hasViolation(payment, "amount"));
        assertTrue(hasViolation(payment, "member"));
        assertTrue(hasViolation(payment, "membership"));
        assertTrue(hasViolation(payment, "paymentDate"));
        assertTrue(hasViolation(payment, "paymentMethod"));
        assertTrue(hasViolation(payment, "paymentStatus"));
        assertFalse(hasViolation(payment, "transactionReference"));

        payment.setMember(new Member());
        payment.setMembership(new Membership());
        payment.setAmount(new BigDecimal("25.50"));
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(PaymentMethod.UPI);
        payment.setPaymentStatus(PaymentStatus.PAID);

        assertTrue(validator.validate(payment).isEmpty());

        payment.setAmount(BigDecimal.ZERO);
        assertTrue(hasViolation(payment, "amount"));
    }

    private static boolean hasViolation(Payment payment, String propertyName) {
        return validator.validate(payment).stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals(propertyName));
    }
}
