package com.gymops.entity;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AttendanceTest {

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
    void allowsMissingCheckOutAndInitializesCreatedAt() {
        Attendance attendance = new Attendance();
        attendance.setMember(new Member());
        attendance.setCheckInTime(LocalDateTime.of(2026, 10, 7, 9, 0));
        attendance.setAttendanceDate(LocalDate.of(2026, 10, 7));

        attendance.onCreate();

        assertNotNull(attendance.getCreatedAt());
        assertTrue(validator.validate(attendance).isEmpty());
    }

    @Test
    void rejectsCheckOutBeforeCheckIn() {
        Attendance attendance = new Attendance();
        attendance.setMember(new Member());
        attendance.setCheckInTime(LocalDateTime.of(2026, 10, 7, 9, 0));
        attendance.setAttendanceDate(LocalDate.of(2026, 10, 7));
        attendance.setCheckOutTime(LocalDateTime.of(2026, 10, 7, 8, 59));

        assertThrows(IllegalStateException.class, attendance::onCreate);
    }
}
