package com.gymops.entity;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MemberTest {

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
    void validatesRequiredFieldsAndEmailFormat() {
        Member member = new Member();

        assertTrue(validator.validate(member).stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("firstName")));
        assertTrue(validator.validate(member).stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("phone")));
        assertTrue(validator.validate(member).stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("joinDate")));

        member.setFirstName("Asha");
        member.setPhone("+15551234567");
        member.setJoinDate(LocalDate.now());
        member.setEmail("not-an-email");

        assertTrue(validator.validate(member).stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("email")));

        member.setEmail("asha@example.com");
        assertFalse(validator.validate(member).stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("email")));
    }
}
