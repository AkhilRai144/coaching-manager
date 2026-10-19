package com.akhilesh.coachingManager.controller;


import com.akhilesh.coachingManager.dto.paymentdto.PaymentRequest;
import com.akhilesh.coachingManager.dto.paymentdto.PaymentResponse;
import com.akhilesh.coachingManager.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> recordPayment(
            @Valid @RequestBody PaymentRequest request) {
        return new ResponseEntity<>(paymentService.recordPayment(request), HttpStatus.CREATED);
    }

    @GetMapping("/due/{feeDueId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByDue(@PathVariable Long feeDueId) {
        return ResponseEntity.ok(paymentService.getPaymentsByDue(feeDueId));
    }
}
