package com.akhilesh.coachingManager.dto.paymentdto;

import com.akhilesh.coachingManager.entity.PaymentMode;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequest {

    @NotNull(message = "Fee due id is required")
    private Long feeDueId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    @Digits(integer = 8, fraction = 2, message = "Amount can have at most 2 decimal places")
    private BigDecimal amountPaid;

    @NotNull(message = "Payment mode is required")
    private PaymentMode mode;
}