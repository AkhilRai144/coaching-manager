package com.akhilesh.coachingManager.service;

import com.akhilesh.coachingManager.dto.PaymentRequest;
import com.akhilesh.coachingManager.dto.PaymentResponse;
import com.akhilesh.coachingManager.entity.Enrollment;
import com.akhilesh.coachingManager.entity.FeeDue;
import com.akhilesh.coachingManager.entity.FeeStatus;
import com.akhilesh.coachingManager.entity.Payment;
import com.akhilesh.coachingManager.entity.PaymentMode;
import com.akhilesh.coachingManager.entity.Student;
import com.akhilesh.coachingManager.exception.InvalidRequestException;
import com.akhilesh.coachingManager.exception.ResourceNotFoundException;
import com.akhilesh.coachingManager.repository.FeeDueRepository;
import com.akhilesh.coachingManager.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private FeeDueRepository feeDueRepository;

    @InjectMocks
    private PaymentService paymentService;

    private FeeDue buildDue(String amount, FeeStatus status, boolean studentDeleted) {
        Student student = new Student();
        student.setId(5L);
        student.setName("Rahul");
        student.setDeleted(studentDeleted);

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);

        FeeDue due = new FeeDue();
        due.setId(1L);
        due.setEnrollment(enrollment);
        due.setAmount(new BigDecimal(amount));
        due.setStatus(status);
        return due;
    }

    private PaymentRequest buildRequest(String amount) {
        PaymentRequest request = new PaymentRequest();
        request.setFeeDueId(1L);
        request.setAmountPaid(new BigDecimal(amount));
        request.setMode(PaymentMode.CASH);
        return request;
    }

    @Test
    void partialPayment_keepsDuePending() {
        FeeDue due = buildDue("1500.00", FeeStatus.PENDING, false);
        when(feeDueRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(due));
        when(paymentRepository.sumPaidByFeeDueId(1L)).thenReturn(null);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentResponse response = paymentService.recordPayment(buildRequest("600"));

        assertThat(response.getRemainingAmount()).isEqualByComparingTo("900");
        assertThat(response.getFeeStatus()).isEqualTo(FeeStatus.PENDING);
        verify(feeDueRepository, never()).save(any());
    }

    @Test
    void finalPayment_marksDuePaid() {
        FeeDue due = buildDue("1500.00", FeeStatus.PENDING, false);
        when(feeDueRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(due));
        when(paymentRepository.sumPaidByFeeDueId(1L)).thenReturn(new BigDecimal("600.00"));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentResponse response = paymentService.recordPayment(buildRequest("900"));

        assertThat(response.getRemainingAmount()).isEqualByComparingTo("0");
        assertThat(response.getFeeStatus()).isEqualTo(FeeStatus.PAID);
        verify(feeDueRepository).save(due);
    }

    @Test
    void paymentMoreThanPending_isRejected() {
        FeeDue due = buildDue("1500.00", FeeStatus.PENDING, false);
        when(feeDueRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(due));
        when(paymentRepository.sumPaidByFeeDueId(1L)).thenReturn(null);

        assertThatThrownBy(() -> paymentService.recordPayment(buildRequest("2000")))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("exceeds");

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void alreadyPaidDue_isRejected() {
        FeeDue due = buildDue("1500.00", FeeStatus.PAID, false);
        when(feeDueRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(due));

        assertThatThrownBy(() -> paymentService.recordPayment(buildRequest("100")))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("already fully paid");

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void missingDue_throwsNotFound() {
        when(feeDueRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.recordPayment(buildRequest("100")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deletedStudentsDue_throwsNotFound() {
        FeeDue due = buildDue("1500.00", FeeStatus.PENDING, true);
        when(feeDueRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(due));

        assertThatThrownBy(() -> paymentService.recordPayment(buildRequest("100")))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(paymentRepository, never()).save(any());
    }
}