package com.gymops.entity;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MembershipTest {

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
    void calculatesEndDateFromPlanDurationAndInitializesTimestamps() {
        LocalDate startDate = LocalDate.of(2026, 10, 7);
        MembershipPlan plan = new MembershipPlan();
        plan.setDurationInMonths(3);
        Membership membership = new Membership();
        membership.setMember(new Member());
        membership.setMembershipPlan(plan);
        membership.setStartDate(startDate);

        membership.onCreate();

        assertEquals(startDate.plusMonths(3), membership.getEndDate());
        assertEquals(MembershipStatus.ACTIVE, membership.getStatus());
        assertNotNull(membership.getCreatedAt());
        assertNotNull(membership.getUpdatedAt());
        assertTrue(validator.validate(membership).isEmpty());

        membership.setStartDate(startDate.plusDays(1));
        membership.onUpdate();

        assertEquals(startDate.plusDays(1).plusMonths(3), membership.getEndDate());
    }

    @Test
    void rejectsPersistingMembershipWithoutRequiredAssociationsOrStartDate() {
        Membership membership = new Membership();

        assertThrows(IllegalStateException.class, membership::onCreate);
        assertFalse(validator.validate(membership).isEmpty());
    }
}
