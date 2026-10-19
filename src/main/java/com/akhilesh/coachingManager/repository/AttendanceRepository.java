package com.akhilesh.coachingManager.repository;

import com.akhilesh.coachingManager.entity.Attendance;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance,Long> {

    boolean existsByEnrollmentBatchIdAndAttendanceDate(Long batchId, LocalDate attendanceDate);

    @EntityGraph(attributePaths = {"enrollment.student", "enrollment.batch"})
    List<Attendance> findByEnrollmentBatchIdAndAttendanceDate(Long batchId, LocalDate attendanceDate);
}
