package com.gymops.service;

import com.gymops.dto.attendance.AttendanceCreateRequest;
import com.gymops.dto.attendance.AttendanceResponse;
import com.gymops.dto.attendance.AttendanceUpdateRequest;

import java.util.List;

public interface AttendanceService {

    AttendanceResponse createAttendance(AttendanceCreateRequest request);

    AttendanceResponse getAttendanceById(Long id);

    List<AttendanceResponse> getAllAttendances();

    AttendanceResponse updateAttendance(Long id, AttendanceUpdateRequest request);

    void deleteAttendance(Long id);
}
