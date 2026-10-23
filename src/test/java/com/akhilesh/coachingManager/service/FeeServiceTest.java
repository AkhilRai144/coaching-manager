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

    private final LocalDate today = LocalDate.now();

    private Batch buildBatch() {
        Batch batch = new Batch();
        batch.setId(1L);
        batch.setName("Class 10 Maths");
        batch.setMonthlyFee(new BigDecimal("1500.00"));
        return batch;
    }

    private Enrollment buildEnrollment(
            Long id, boolean studentDeleted, Batch batch, LocalDate joinDate) {
        Student student = new Student();
        student.setId(id + 100);
        student.setName("Student " + id);
        student.setDeleted(studentDeleted);

        Enrollment enrollment = new Enrollment();
        enrollment.setId(id);
        enrollment.setStudent(student);
        enrollment.setBatch(batch);
        enrollment.setJoinDate(joinDate);
        return enrollment;
    }

    private GenerateFeesRequest buildRequest() {
        GenerateFeesRequest request = new GenerateFeesRequest();
        request.setBatchId(1L);
        return request;
    }

    private void stub(Batch batch, List<FeeDue> existing, List<Enrollment> enrollments) {
        when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));
        when(feeDueRepository.findByEnrollmentBatchIdOrderByPeriodStartDesc(1L))
                .thenReturn(existing);
        when(enrollmentRepository.findByBatchId(1L)).thenReturn(enrollments);
    }

    @Test
    void newStudent_getsOnlyFirstCycleStartingOnJoinDate() {
        Batch batch = buildBatch();
        Enrollment e1 = buildEnrollment(1L, false, batch, today);
        stub(batch, List.of(), List.of(e1));

        GenerateFeesResponse response = feeService.generateFees(buildRequest());

        assertThat(response.getCreated()).isEqualTo(1);
        verify(feeDueRepository).saveAll(duesCaptor.capture());
        FeeDue due = duesCaptor.getValue().get(0);
        assertThat(due.getPeriodStart()).isEqualTo(today);
        assertThat(due.getPeriodEnd()).isEqualTo(today.plusMonths(1).minusDays(1));
        assertThat(due.getAmount()).isEqualByComparingTo("1500");
        assertThat(due.getStatus()).isEqualTo(FeeStatus.PENDING);
    }

    @Test
    void studentJoinedTwoMonthsAgo_getsThreeCyclesFromJoinDate() {
        Batch batch = buildBatch();
        LocalDate join = today.minusMonths(2);
        Enrollment e1 = buildEnrollment(1L, false, batch, join);
        stub(batch, List.of(), List.of(e1));

        GenerateFeesResponse response = feeService.generateFees(buildRequest());

        assertThat(response.getCreated()).isEqualTo(3);
        verify(feeDueRepository).saveAll(duesCaptor.capture());
        assertThat(duesCaptor.getValue())
                .extracting(FeeDue::getPeriodStart)
                .containsExactly(join, join.plusMonths(1), join.plusMonths(2));
    }

    @Test
    void existingCycleIsNotBilledAgain() {
        Batch batch = buildBatch();
        LocalDate join = today.minusMonths(1);
        Enrollment e1 = buildEnrollment(1L, false, batch, join);

        FeeDue firstCycle = new FeeDue();
        firstCycle.setEnrollment(e1);
        firstCycle.setPeriodStart(join);
        stub(batch, List.of(firstCycle), List.of(e1));

        GenerateFeesResponse response = feeService.generateFees(buildRequest());

        assertThat(response.getCreated()).isEqualTo(1);
        assertThat(response.getAlreadyExisted()).isEqualTo(1);
        verify(feeDueRepository).saveAll(duesCaptor.capture());
        assertThat(duesCaptor.getValue().get(0).getPeriodStart())
                .isEqualTo(join.plusMonths(1));
    }

    @Test
    void deletedStudentIsSkipped() {
        Batch batch = buildBatch();
        Enrollment active = buildEnrollment(1L, false, batch, today);
        Enrollment deleted = buildEnrollment(2L, true, batch, today);
        stub(batch, List.of(), List.of(active, deleted));

        GenerateFeesResponse response = feeService.generateFees(buildRequest());

        assertThat(response.getCreated()).isEqualTo(1);
        verify(feeDueRepository).saveAll(duesCaptor.capture());
        assertThat(duesCaptor.getValue().get(0).getEnrollment()).isEqualTo(active);
    }

    @Test
    void missingBatch_throwsNotFound() {
        when(batchRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> feeService.generateFees(buildRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}