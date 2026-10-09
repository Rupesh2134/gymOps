package com.gymops.controller;

import com.gymops.dto.attendance.AttendanceResponse;
import com.gymops.dto.attendance.AttendanceUpdateRequest;
import com.gymops.service.AttendanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttendanceController.class)
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AttendanceService attendanceService;

    @Test
    void getAllAttendances_returnsAttendanceDtos() throws Exception {
        AttendanceResponse attendance = new AttendanceResponse();
        attendance.setId(1L);
        attendance.setMemberName("Asha");
        when(attendanceService.getAllAttendances()).thenReturn(List.of(attendance));

        mockMvc.perform(get("/api/attendance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].memberName").value("Asha"));
    }

    @Test
    void getAttendanceById_returnsAttendanceDto() throws Exception {
        AttendanceResponse attendance = new AttendanceResponse();
        attendance.setId(1L);
        when(attendanceService.getAttendanceById(1L)).thenReturn(attendance);

        mockMvc.perform(get("/api/attendance/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void checkIn_returnsCreatedAndValidatesRequest() throws Exception {
        AttendanceResponse attendance = new AttendanceResponse();
        attendance.setId(1L);
        when(attendanceService.createAttendance(any())).thenReturn(attendance);

        mockMvc.perform(post("/api/attendance/check-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"memberId":1,"checkInTime":"2025-01-01T09:00:00","attendanceDate":"2025-01-01"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        mockMvc.perform(post("/api/attendance/check-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"memberId":1}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkOut_updatesOnlyCheckoutTime() throws Exception {
        AttendanceResponse attendance = new AttendanceResponse();
        attendance.setId(1L);
        attendance.setCheckOutTime(LocalDateTime.parse("2025-01-01T17:00:00"));
        when(attendanceService.updateAttendance(eq(1L), any())).thenReturn(attendance);

        mockMvc.perform(patch("/api/attendance/1/check-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"checkOutTime":"2025-01-01T17:00:00","memberId":999}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checkOutTime").value("2025-01-01T17:00:00"));

        verify(attendanceService).updateAttendance(eq(1L), org.mockito.ArgumentMatchers.argThat(
                (AttendanceUpdateRequest request) -> request.getCheckOutTime() != null
                        && request.getMemberId() == null
                        && request.getCheckInTime() == null
                        && request.getAttendanceDate() == null));
    }

    @Test
    void checkOut_requiresCheckoutTime() throws Exception {
        mockMvc.perform(patch("/api/attendance/1/check-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
