package com.gymops.service;

import com.gymops.dto.attendance.AttendanceCreateRequest;
import com.gymops.dto.attendance.AttendanceResponse;
import com.gymops.dto.attendance.AttendanceUpdateRequest;
import com.gymops.entity.Attendance;
import com.gymops.entity.Member;
import com.gymops.entity.MemberStatus;
import com.gymops.exception.BusinessRuleException;
import com.gymops.exception.ResourceNotFoundException;
import com.gymops.repository.AttendanceRepository;
import com.gymops.repository.MemberRepository;
import com.gymops.service.impl.AttendanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    @BeforeEach
    void setUp() {
        attendanceService = new AttendanceServiceImpl(attendanceRepository, memberRepository);
    }

    @Test
    void createAttendance_shouldSaveAndReturnResponse() {
        Member member = new Member();
        setMemberId(member, 1L);
        member.setStatus(MemberStatus.ACTIVE);

        AttendanceCreateRequest request = new AttendanceCreateRequest();
        request.setMemberId(1L);
        request.setCheckInTime(LocalDateTime.of(2025, 1, 5, 8, 0));
        request.setCheckOutTime(LocalDateTime.of(2025, 1, 5, 9, 30));
        request.setAttendanceDate(LocalDate.of(2025, 1, 5));

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(attendanceRepository.existsByMemberAndAttendanceDate(member, LocalDate.of(2025, 1, 5))).thenReturn(false);
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(invocation -> {
            Attendance saved = invocation.getArgument(0);
            setAttendanceId(saved, 44L);
            return saved;
        });

        AttendanceResponse response = attendanceService.createAttendance(request);

        assertNotNull(response);
        assertEquals(44L, response.getId());
        assertEquals(1L, response.getMemberId());
    }

    @Test
    void createAttendance_shouldRejectDuplicateForSameDay() {
        Member member = new Member();
        setMemberId(member, 1L);
        member.setStatus(MemberStatus.ACTIVE);

        AttendanceCreateRequest request = new AttendanceCreateRequest();
        request.setMemberId(1L);
        request.setCheckInTime(LocalDateTime.of(2025, 1, 5, 8, 0));
        request.setCheckOutTime(LocalDateTime.of(2025, 1, 5, 9, 30));
        request.setAttendanceDate(LocalDate.of(2025, 1, 5));

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(attendanceRepository.existsByMemberAndAttendanceDate(member, LocalDate.of(2025, 1, 5))).thenReturn(true);

        assertThrows(com.gymops.exception.DuplicateResourceException.class, () -> attendanceService.createAttendance(request));
    }

    @Test
    void getAttendanceById_shouldReturnAttendance() {
        Attendance attendance = new Attendance();
        setAttendanceId(attendance, 12L);
        attendance.setMember(new Member());
        setMemberId(attendance.getMember(), 3L);
        attendance.setCheckInTime(LocalDateTime.of(2025, 2, 1, 8, 10));
        attendance.setCheckOutTime(LocalDateTime.of(2025, 2, 1, 9, 10));
        attendance.setAttendanceDate(LocalDate.of(2025, 2, 1));

        when(attendanceRepository.findById(12L)).thenReturn(Optional.of(attendance));

        AttendanceResponse response = attendanceService.getAttendanceById(12L);

        assertEquals(12L, response.getId());
        assertEquals(3L, response.getMemberId());
    }

    @Test
    void updateAttendance_shouldRejectCheckoutBeforeCheckin() {
        Attendance attendance = new Attendance();
        setAttendanceId(attendance, 13L);
        attendance.setMember(new Member());
        setMemberId(attendance.getMember(), 4L);
        attendance.setCheckInTime(LocalDateTime.of(2025, 2, 1, 10, 0));
        attendance.setCheckOutTime(LocalDateTime.of(2025, 2, 1, 9, 0));
        attendance.setAttendanceDate(LocalDate.of(2025, 2, 1));

        AttendanceUpdateRequest request = new AttendanceUpdateRequest();
        request.setCheckInTime(LocalDateTime.of(2025, 2, 1, 10, 0));
        request.setCheckOutTime(LocalDateTime.of(2025, 2, 1, 9, 0));

        when(attendanceRepository.findById(13L)).thenReturn(Optional.of(attendance));

        assertThrows(BusinessRuleException.class, () -> attendanceService.updateAttendance(13L, request));
    }

    @Test
    void deleteAttendance_shouldRemoveRecord() {
        Attendance attendance = new Attendance();
        setAttendanceId(attendance, 15L);
        attendance.setMember(new Member());
        setMemberId(attendance.getMember(), 5L);

        when(attendanceRepository.findById(15L)).thenReturn(Optional.of(attendance));

        attendanceService.deleteAttendance(15L);

        verify(attendanceRepository).delete(attendance);
    }

    @Test
    void getAllAttendances_shouldReturnList() {
        Attendance attendance = new Attendance();
        setAttendanceId(attendance, 16L);
        attendance.setMember(new Member());
        setMemberId(attendance.getMember(), 6L);
        attendance.setCheckInTime(LocalDateTime.of(2025, 3, 1, 8, 0));
        attendance.setCheckOutTime(LocalDateTime.of(2025, 3, 1, 9, 0));
        attendance.setAttendanceDate(LocalDate.of(2025, 3, 1));

        when(attendanceRepository.findAll()).thenReturn(List.of(attendance));

        List<AttendanceResponse> result = attendanceService.getAllAttendances();

        assertEquals(1, result.size());
        assertEquals(6L, result.get(0).getMemberId());
    }

    @Test
    void getAttendanceById_shouldThrowWhenMissing() {
        when(attendanceRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> attendanceService.getAttendanceById(404L));
    }

    private void setMemberId(Member member, long id) {
        try {
            Field field = Member.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(member, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void setAttendanceId(Attendance attendance, long id) {
        try {
            Field field = Attendance.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(attendance, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
