package com.gymops.dto.attendance;

import com.gymops.entity.Attendance;
import com.gymops.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Member and date-time information for a gym check-in.")
public class AttendanceCreateRequest {

    @Schema(description = "ID of the member checking in.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Member id is required")
    private Long memberId;

    @Schema(description = "Check-in timestamp in ISO-8601 local date-time format.",
            example = "2026-10-09T09:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Check-in time is required")
    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    @Schema(description = "Attendance calendar date.", example = "2026-10-09",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Attendance date is required")
    private LocalDate attendanceDate;

    public AttendanceCreateRequest() {
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public Attendance toEntity(Member member) {
        Attendance attendance = new Attendance();
        attendance.setMember(member);
        attendance.setCheckInTime(this.checkInTime);
        attendance.setCheckOutTime(this.checkOutTime);
        attendance.setAttendanceDate(this.attendanceDate);
        return attendance;
    }
}
