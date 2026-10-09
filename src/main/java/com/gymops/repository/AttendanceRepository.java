package com.gymops.repository;

import com.gymops.entity.Attendance;
import com.gymops.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    long countByAttendanceDate(LocalDate attendanceDate);

    List<Attendance> findByMember(Member member);

    Attendance findByMemberAndAttendanceDate(Member member, LocalDate attendanceDate);

    boolean existsByMemberAndAttendanceDate(Member member, LocalDate attendanceDate);
}
