package com.akhilesh.coachingManager.dto;


import com.akhilesh.coachingManager.entity.FeeStatus;
import com.akhilesh.coachingManager.entity.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private Long feeDueId;
    private Long studentId;
    private String studentName;
    private BigDecimal amountPaid;
    private LocalDate paidOn;
    private PaymentMode mode;
    private BigDecimal remainingAmount;
    private FeeStatus feeStatus;
}
