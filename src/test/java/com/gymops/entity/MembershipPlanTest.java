package com.gymops.entity;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MembershipPlanTest {

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
    void validatesRequiredNamePositiveDurationAndPositivePrice() {
        MembershipPlan plan = new MembershipPlan();

        assertEquals(Boolean.TRUE, plan.getActive());
        assertTrue(hasViolation(plan, "name"));
        assertTrue(hasViolation(plan, "durationInMonths"));
        assertTrue(hasViolation(plan, "price"));

        plan.setName("Flexible Membership");
        plan.setDurationInMonths(6);
        plan.setPrice(new BigDecimal("49.99"));

        assertFalse(hasViolation(plan, "name"));
        assertFalse(hasViolation(plan, "durationInMonths"));
        assertFalse(hasViolation(plan, "price"));

        plan.setDurationInMonths(0);
        plan.setPrice(BigDecimal.ZERO);

        assertTrue(hasViolation(plan, "durationInMonths"));
        assertTrue(hasViolation(plan, "price"));
    }

    private static boolean hasViolation(MembershipPlan plan, String propertyName) {
        return validator.validate(plan).stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals(propertyName));
    }
}
