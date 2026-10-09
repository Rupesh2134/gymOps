package com.gymops.service.impl;

import com.gymops.dto.attendance.AttendanceCreateRequest;
import com.gymops.dto.attendance.AttendanceResponse;
import com.gymops.dto.attendance.AttendanceUpdateRequest;
import com.gymops.entity.Attendance;
import com.gymops.entity.Member;
import com.gymops.exception.BusinessRuleException;
import com.gymops.exception.DuplicateResourceException;
import com.gymops.exception.ResourceNotFoundException;
import com.gymops.repository.AttendanceRepository;
import com.gymops.repository.MemberRepository;
import com.gymops.service.AttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository, MemberRepository memberRepository) {
        this.attendanceRepository = attendanceRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional
    public AttendanceResponse createAttendance(AttendanceCreateRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Attendance request must not be null");
        }

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + request.getMemberId()));

        if (request.getCheckInTime() == null || request.getAttendanceDate() == null) {
            throw new BusinessRuleException("Check-in time and attendance date are required");
        }

        if (attendanceRepository.existsByMemberAndAttendanceDate(member, request.getAttendanceDate())) {
            throw new DuplicateResourceException("Attendance already exists for this member on the selected date");
        }

        if (request.getCheckOutTime() != null && request.getCheckOutTime().isBefore(request.getCheckInTime())) {
            throw new BusinessRuleException("Check-out time cannot be before check-in time");
        }

        Attendance attendance = request.toEntity(member);
        Attendance savedAttendance = attendanceRepository.save(attendance);
        return AttendanceResponse.fromEntity(savedAttendance);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceResponse getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with id: " + id));
        return AttendanceResponse.fromEntity(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAllAttendances() {
        return attendanceRepository.findAll().stream()
                .map(AttendanceResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public AttendanceResponse updateAttendance(Long id, AttendanceUpdateRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Attendance update request must not be null");
        }

        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with id: " + id));

        if (request.getMemberId() != null) {
            Member member = memberRepository.findById(request.getMemberId())
                    .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + request.getMemberId()));
            attendance.setMember(member);
        }

        if (request.getCheckInTime() != null) {
            attendance.setCheckInTime(request.getCheckInTime());
        }

        if (request.getCheckOutTime() != null) {
            if (attendance.getCheckInTime() != null && request.getCheckOutTime().isBefore(attendance.getCheckInTime())) {
                throw new BusinessRuleException("Check-out time cannot be before check-in time");
            }
            attendance.setCheckOutTime(request.getCheckOutTime());
        }

        if (request.getAttendanceDate() != null) {
            LocalDate newDate = request.getAttendanceDate();
            Member member = attendance.getMember();
            if (member != null && attendanceRepository.existsByMemberAndAttendanceDate(member, newDate)) {
                Attendance existing = attendanceRepository.findByMemberAndAttendanceDate(member, newDate);
                if (existing != null && !existing.getId().equals(attendance.getId())) {
                    throw new DuplicateResourceException("Attendance already exists for this member on the selected date");
                }
            }
            attendance.setAttendanceDate(newDate);
        }

        Attendance updatedAttendance = attendanceRepository.save(attendance);
        return AttendanceResponse.fromEntity(updatedAttendance);
    }

    @Override
    @Transactional
    public void deleteAttendance(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with id: " + id));

        attendanceRepository.delete(attendance);
    }
}
