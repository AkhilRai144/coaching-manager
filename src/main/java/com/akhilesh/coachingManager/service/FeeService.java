package com.akhilesh.coachingManager.service;

import com.akhilesh.coachingManager.audit.Audited;
import com.akhilesh.coachingManager.dto.FeeDueResponse;
import com.akhilesh.coachingManager.dto.GenerateFeesRequest;
import com.akhilesh.coachingManager.dto.GenerateFeesResponse;
import com.akhilesh.coachingManager.entity.*;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
                due.getFeeMonth(),
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

    @Transactional
    @Audited("FEES_GENERATED")
    public GenerateFeesResponse generateFees(GenerateFeesRequest request) {

        Batch batch = batchRepository.findById(request.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Batch not found with id " + request.getBatchId()));

        LocalDate month = request.getFeeMonth().withDayOfMonth(1);

        List<FeeDue> existingDues =
                feeDueRepository.findByEnrollmentBatchIdAndFeeMonth(batch.getId(), month);

        Set<Long> alreadyBilled = existingDues.stream()
                .map(due -> due.getEnrollment().getId())
                .collect(Collectors.toSet());

        int created = 0;
        int alreadyExisted = 0;
        List<FeeDue> newDues = new ArrayList<>();

        for (Enrollment enrollment : enrollmentRepository.findByBatchId(batch.getId())) {

            if (enrollment.getStudent().isDeleted()) {
                continue;
            }

            if (alreadyBilled.contains(enrollment.getId())) {
                alreadyExisted++;
                continue;
            }

            FeeDue due = new FeeDue();
            due.setEnrollment(enrollment);
            due.setFeeMonth(month);
            due.setAmount(batch.getMonthlyFee());
            newDues.add(due);
            created++;
        }

        feeDueRepository.saveAll(newDues);

        return new GenerateFeesResponse(created, alreadyExisted);
    }

    @Transactional(readOnly = true)
    public List<FeeDueResponse> getDuesByBatchAndMonth(Long batchId, LocalDate month) {

        if (!batchRepository.existsById(batchId)) {
            throw new ResourceNotFoundException("Batch not found with id " + batchId);
        }

        return feeDueRepository
                .findByEnrollmentBatchIdAndFeeMonth(batchId, month.withDayOfMonth(1))
                .stream()
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
        return feeDueRepository.findByStatus(FeeStatus.PENDING)
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
