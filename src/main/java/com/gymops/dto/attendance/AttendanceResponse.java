package com.gymops.dto.attendance;

import com.gymops.entity.Attendance;
import com.gymops.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Member attendance and check-in/check-out details.")
public class AttendanceResponse {

    private Long id;
    private Long memberId;
    private String memberName;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private LocalDate attendanceDate;
    private LocalDateTime createdAt;

    public AttendanceResponse() {
    }

    public static AttendanceResponse fromEntity(Attendance attendance) {
        if (attendance == null) {
            return null;
        }

        AttendanceResponse response = new AttendanceResponse();
        response.setId(attendance.getId());

        Member member = attendance.getMember();
        if (member != null) {
            response.setMemberId(member.getId());
            response.setMemberName(member.getFirstName() + (member.getLastName() != null ? " " + member.getLastName() : ""));
        }

        response.setCheckInTime(attendance.getCheckInTime());
        response.setCheckOutTime(attendance.getCheckOutTime());
        response.setAttendanceDate(attendance.getAttendanceDate());
        response.setCreatedAt(attendance.getCreatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
