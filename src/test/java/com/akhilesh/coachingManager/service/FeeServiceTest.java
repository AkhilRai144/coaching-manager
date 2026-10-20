package com.akhilesh.coachingManager.service;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeeServiceTest {

    @Mock
    private FeeDueRepository feeDueRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private FeeService feeService;

    @Captor
    private ArgumentCaptor<List<FeeDue>> duesCaptor;

    private Batch buildBatch() {
        Batch batch = new Batch();
        batch.setId(1L);
        batch.setName("Class 10 Maths");
        batch.setMonthlyFee(new BigDecimal("1500.00"));
        return batch;
    }

    private Enrollment buildEnrollment(Long enrollmentId, boolean studentDeleted, Batch batch) {
        Student student = new Student();
        student.setId(enrollmentId + 100);
        student.setName("Student " + enrollmentId);
        student.setDeleted(studentDeleted);

        Enrollment enrollment = new Enrollment();
        enrollment.setId(enrollmentId);
        enrollment.setStudent(student);
        enrollment.setBatch(batch);
        return enrollment;
    }

    private GenerateFeesRequest buildRequest(LocalDate month) {
        GenerateFeesRequest request = new GenerateFeesRequest();
        request.setBatchId(1L);
        request.setFeeMonth(month);
        return request;
    }

    @Test
    void generateFees_createsDueForEachActiveStudent() {
        Batch batch = buildBatch();
        Enrollment e1 = buildEnrollment(1L, false, batch);
        Enrollment e2 = buildEnrollment(2L, false, batch);

        when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));
        when(feeDueRepository.findByEnrollmentBatchIdAndFeeMonth(1L, LocalDate.of(2026, 10, 1)))
                .thenReturn(List.of());
        when(enrollmentRepository.findByBatchId(1L)).thenReturn(List.of(e1, e2));

        GenerateFeesResponse response =
                feeService.generateFees(buildRequest(LocalDate.of(2026, 10, 1)));

        assertThat(response.getCreated()).isEqualTo(2);
        assertThat(response.getAlreadyExisted()).isEqualTo(0);
    }

    @Test
    void generateFees_skipsDeletedStudent() {
        Batch batch = buildBatch();
        Enrollment active = buildEnrollment(1L, false, batch);
        Enrollment deleted = buildEnrollment(2L, true, batch);

        when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));
        when(feeDueRepository.findByEnrollmentBatchIdAndFeeMonth(1L, LocalDate.of(2026, 10, 1)))
                .thenReturn(List.of());
        when(enrollmentRepository.findByBatchId(1L)).thenReturn(List.of(active, deleted));

        GenerateFeesResponse response =
                feeService.generateFees(buildRequest(LocalDate.of(2026, 10, 1)));

        assertThat(response.getCreated()).isEqualTo(1);
        verify(feeDueRepository).saveAll(duesCaptor.capture());
        assertThat(duesCaptor.getValue()).hasSize(1);
        assertThat(duesCaptor.getValue().get(0).getEnrollment()).isEqualTo(active);
    }

    @Test
    void generateFees_doesNotDuplicateExistingDue() {
        Batch batch = buildBatch();
        Enrollment alreadyBilled = buildEnrollment(1L, false, batch);
        Enrollment notBilled = buildEnrollment(2L, false, batch);

        FeeDue existing = new FeeDue();
        existing.setEnrollment(alreadyBilled);

        when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));
        when(feeDueRepository.findByEnrollmentBatchIdAndFeeMonth(1L, LocalDate.of(2026, 10, 1)))
                .thenReturn(List.of(existing));
        when(enrollmentRepository.findByBatchId(1L))
                .thenReturn(List.of(alreadyBilled, notBilled));

        GenerateFeesResponse response =
                feeService.generateFees(buildRequest(LocalDate.of(2026, 10, 1)));

        assertThat(response.getCreated()).isEqualTo(1);
        assertThat(response.getAlreadyExisted()).isEqualTo(1);
        verify(feeDueRepository).saveAll(duesCaptor.capture());
        assertThat(duesCaptor.getValue().get(0).getEnrollment()).isEqualTo(notBilled);
    }

    @Test
    void generateFees_usesFirstOfMonthAndCopiesBatchFee() {
        Batch batch = buildBatch();
        Enrollment e1 = buildEnrollment(1L, false, batch);

        when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));
        when(feeDueRepository.findByEnrollmentBatchIdAndFeeMonth(1L, LocalDate.of(2026, 10, 1)))
                .thenReturn(List.of());
        when(enrollmentRepository.findByBatchId(1L)).thenReturn(List.of(e1));

        feeService.generateFees(buildRequest(LocalDate.of(2026, 10, 15)));

        verify(feeDueRepository).saveAll(duesCaptor.capture());
        FeeDue saved = duesCaptor.getValue().get(0);
        assertThat(saved.getFeeMonth()).isEqualTo(LocalDate.of(2026, 10, 1));
        assertThat(saved.getAmount()).isEqualByComparingTo("1500");
        assertThat(saved.getStatus()).isEqualTo(FeeStatus.PENDING);
    }

    @Test
    void generateFees_missingBatch_throwsNotFound() {
        when(batchRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> feeService.generateFees(buildRequest(LocalDate.of(2026, 10, 1))))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}