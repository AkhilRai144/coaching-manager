package com.akhilesh.coachingManager.service;



import com.akhilesh.coachingManager.dto.PaymentRequest;
import com.akhilesh.coachingManager.dto.PaymentResponse;
import com.akhilesh.coachingManager.audit.Audited;
import com.akhilesh.coachingManager.entity.FeeDue;
import com.akhilesh.coachingManager.entity.FeeStatus;
import com.akhilesh.coachingManager.entity.Payment;
import com.akhilesh.coachingManager.entity.Student;
import com.akhilesh.coachingManager.exception.InvalidRequestException;
import com.akhilesh.coachingManager.exception.ResourceNotFoundException;
import com.akhilesh.coachingManager.repository.FeeDueRepository;
import com.akhilesh.coachingManager.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final FeeDueRepository feeDueRepository;

    private BigDecimal paidSoFar(Long feeDueId) {
        BigDecimal total = paymentRepository.sumPaidByFeeDueId(feeDueId);
        return total == null ? BigDecimal.ZERO : total;
    }

    private PaymentResponse toResponse(Payment payment, FeeDue due, BigDecimal remaining) {
        Student student = due.getEnrollment().getStudent();

        return new PaymentResponse(
                payment.getId(),
                due.getId(),
                student.getId(),
                student.getName(),
                payment.getAmountPaid(),
                payment.getPaidOn(),
                payment.getMode(),
                remaining,
                due.getStatus(),
                due.getPeriodStart(),
                due.getPeriodEnd());
    }

    @Transactional
    @Audited("PAYMENT_RECORDED")
    public PaymentResponse recordPayment(PaymentRequest request) {

        FeeDue due = feeDueRepository.findByIdForUpdate(request.getFeeDueId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Fee due not found with id " + request.getFeeDueId()));

        if (due.getEnrollment().getStudent().isDeleted()) {
            throw new ResourceNotFoundException(
                    "Fee due not found with id " + request.getFeeDueId());
        }

        if (due.getStatus() == FeeStatus.PAID) {
            throw new InvalidRequestException("This fee is already fully paid");
        }

        BigDecimal alreadyPaid = paidSoFar(due.getId());
        BigDecimal remaining = due.getAmount().subtract(alreadyPaid);

        if (request.getAmountPaid().compareTo(remaining) > 0) {
            throw new InvalidRequestException(
                    "Payment exceeds pending amount. Pending: " + remaining);
        }

        Payment payment = new Payment();
        payment.setFeeDue(due);
        payment.setAmountPaid(request.getAmountPaid());
        payment.setPaidOn(LocalDate.now());
        payment.setMode(request.getMode());
        Payment savedPayment = paymentRepository.save(payment);

        BigDecimal newRemaining = remaining.subtract(request.getAmountPaid());

        if (newRemaining.compareTo(BigDecimal.ZERO) == 0) {
            due.setStatus(FeeStatus.PAID);
            feeDueRepository.save(due);
        }

        return toResponse(savedPayment, due, newRemaining);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByDue(Long feeDueId) {

        FeeDue due = feeDueRepository.findById(feeDueId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Fee due not found with id " + feeDueId));

        if (due.getEnrollment().getStudent().isDeleted()) {
            throw new ResourceNotFoundException("Fee due not found with id " + feeDueId);
        }

        BigDecimal remaining = due.getAmount().subtract(paidSoFar(feeDueId));

        return paymentRepository.findByFeeDueId(feeDueId)
                .stream()
                .map(p -> toResponse(p, due, remaining))
                .toList();
    }
}