package com.akhilesh.coachingManager.service;

import com.akhilesh.coachingManager.audit.Audited;
import com.akhilesh.coachingManager.dto.FeeDueResponse;
import com.akhilesh.coachingManager.dto.GenerateFeesRequest;
import com.akhilesh.coachingManager.dto.GenerateFeesResponse;
import com.akhilesh.coachingManager.entity.Batch;
import com.akhilesh.coachingManager.entity.Enrollment;
import com.akhilesh.coachingManager.entity.FeeDue;
import com.akhilesh.coachingManager.entity.FeeStatus;
import com.akhilesh.coachingManager.entity.Student;
import com.akhilesh.coachingManager.exception.ResourceNotFoundException;
import com.akhilesh.coachingManager.repository.BatchRepository;
import com.akhilesh.coachingManager.repository.EnrollmentRepository;
import com.akhilesh.coachingManager.repository.FeeDueRepository;
import com.akhilesh.coachingManager.repository.StudentRepository;
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
public class FeeService {

    private final FeeDueRepository feeDueRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final BatchRepository batchRepository;
    private final StudentRepository studentRepository;

    private FeeDueResponse toResponse(FeeDue due) {
        Student student = due.getEnrollment().getStudent();
        Batch batch = due.getEnrollment().getBatch();

        return new FeeDueResponse(
                due.getId(),
                student.getId(),
                student.getName(),
                batch.getId(),
                batch.getName(),
                due.getPeriodStart(),
                due.getPeriodEnd(),
                due.getAmount(),
                due.getStatus());
    }

    private Student findActiveStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));

        if (student.isDeleted()) {
            throw new ResourceNotFoundException("Student not found with id " + id);
        }

        return student;
    }

    @Audited("FEES_GENERATED")
    @Transactional
    public GenerateFeesResponse generateFees(GenerateFeesRequest request) {

        Batch batch = batchRepository.findById(request.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Batch not found with id " + request.getBatchId()));

        LocalDate today = LocalDate.now();

        // enrollment id -> us enrollment ke jin cycles ki due pehle se bani hai unki start dates
        Map<Long, Set<LocalDate>> alreadyBilled = new HashMap<>();
        for (FeeDue due : feeDueRepository
                .findByEnrollmentBatchIdOrderByPeriodStartDesc(batch.getId())) {
            alreadyBilled
                    .computeIfAbsent(due.getEnrollment().getId(), k -> new HashSet<>())
                    .add(due.getPeriodStart());
        }

        int created = 0;
        int alreadyExisted = 0;
        List<FeeDue> newDues = new ArrayList<>();

        for (Enrollment enrollment : enrollmentRepository.findByBatchId(batch.getId())) {

            if (enrollment.getStudent().isDeleted()) {
                continue;
            }

            Set<LocalDate> billed = alreadyBilled.getOrDefault(enrollment.getId(), Set.of());
            LocalDate joinDate = enrollment.getJoinDate();

            for (int n = 0; ; n++) {
                LocalDate periodStart = joinDate.plusMonths(n);

                if (periodStart.isAfter(today)) {
                    break;
                }

                if (billed.contains(periodStart)) {
                    alreadyExisted++;
                    continue;
                }

                FeeDue due = new FeeDue();
                due.setEnrollment(enrollment);
                due.setPeriodStart(periodStart);
                due.setPeriodEnd(joinDate.plusMonths(n + 1).minusDays(1));
                due.setAmount(batch.getMonthlyFee());
                newDues.add(due);
                created++;
            }
        }

        feeDueRepository.saveAll(newDues);

        return new GenerateFeesResponse(created, alreadyExisted);
    }

    @Transactional(readOnly = true)
    public List<FeeDueResponse> getDuesByBatch(Long batchId) {

        if (!batchRepository.existsById(batchId)) {
            throw new ResourceNotFoundException("Batch not found with id " + batchId);
        }

        return feeDueRepository.findByEnrollmentBatchIdOrderByPeriodStartDesc(batchId)
                .stream()
                .filter(d -> !d.getEnrollment().getStudent().isDeleted())
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FeeDueResponse> getDuesByStudent(Long studentId) {

        findActiveStudent(studentId);

        return feeDueRepository.findByEnrollmentStudentId(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FeeDueResponse> getPendingDues() {
        return feeDueRepository
                .findByStatusAndEnrollmentStudentDeletedFalse(FeeStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FeeDueResponse> getMyDues(String email) {

        Student student = studentRepository.findByUserEmail(email)
                .filter(s -> !s.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        return feeDueRepository.findByEnrollmentStudentId(student.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }
}