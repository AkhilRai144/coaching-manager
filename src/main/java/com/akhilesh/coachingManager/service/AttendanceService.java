package com.akhilesh.coachingManager.service;

import com.akhilesh.coachingManager.dto.attendancedto.AttendanceEntry;
import com.akhilesh.coachingManager.dto.attendancedto.AttendanceRequest;
import com.akhilesh.coachingManager.dto.attendancedto.AttendanceResponse;
import com.akhilesh.coachingManager.entity.Attendance;
import com.akhilesh.coachingManager.entity.Batch;
import com.akhilesh.coachingManager.entity.Enrollment;
import com.akhilesh.coachingManager.entity.Student;
import com.akhilesh.coachingManager.exception.DuplicateResourceException;
import com.akhilesh.coachingManager.exception.InvalidRequestException;
import com.akhilesh.coachingManager.exception.ResourceNotFoundException;
import com.akhilesh.coachingManager.repository.AttendanceRepository;
import com.akhilesh.coachingManager.repository.BatchRepository;
import com.akhilesh.coachingManager.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final BatchRepository batchRepository;

    private AttendanceResponse toResponse(Attendance attendance) {
        Student student = attendance.getEnrollment().getStudent();
        Batch batch = attendance.getEnrollment().getBatch();

        return new AttendanceResponse(
                attendance.getId(),
                student.getId(),
                student.getName(),
                batch.getId(),
                batch.getName(),
                attendance.getAttendanceDate(),
                attendance.getStatus());
    }

    @Transactional
    public List<AttendanceResponse> markAttendance(AttendanceRequest request) {

        if (!batchRepository.existsById(request.getBatchId())) {
            throw new ResourceNotFoundException(
                    "Batch not found with id " + request.getBatchId());
        }

        if (attendanceRepository.existsByEnrollmentBatchIdAndAttendanceDate(
                request.getBatchId(), request.getAttendanceDate())) {
            throw new DuplicateResourceException(
                    "Attendance already marked for this batch on this date");
        }

        Map<Long, Enrollment> enrollmentByStudentId = new HashMap<>();
        for (Enrollment e : enrollmentRepository.findByBatchId(request.getBatchId())) {
            if (!e.getStudent().isDeleted()) {
                enrollmentByStudentId.put(e.getStudent().getId(), e);
            }
        }

        Set<Long> seen = new HashSet<>();
        List<Attendance> records = new ArrayList<>();

        for (AttendanceEntry entry : request.getEntries()) {
            if (!seen.add(entry.getStudentId())) {
                throw new InvalidRequestException(
                        "Student " + entry.getStudentId() + " is repeated in the request");
            }

            Enrollment enrollment = enrollmentByStudentId.get(entry.getStudentId());
            if (enrollment == null) {
                throw new InvalidRequestException(
                        "Student " + entry.getStudentId() + " is not enrolled in this batch");
            }

            Attendance attendance = new Attendance();
            attendance.setEnrollment(enrollment);
            attendance.setAttendanceDate(request.getAttendanceDate());
            attendance.setStatus(entry.getStatus());
            records.add(attendance);
        }

        List<Attendance> saved = attendanceRepository.saveAll(records);

        return saved.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendance(Long batchId, LocalDate date) {

        if (!batchRepository.existsById(batchId)) {
            throw new ResourceNotFoundException("Batch not found with id " + batchId);
        }

        return attendanceRepository
                .findByEnrollmentBatchIdAndAttendanceDate(batchId, date)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
