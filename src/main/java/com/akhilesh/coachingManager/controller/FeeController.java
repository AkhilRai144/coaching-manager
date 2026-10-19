package com.akhilesh.coachingManager.controller;

import com.akhilesh.coachingManager.dto.feedto.FeeDueResponse;
import com.akhilesh.coachingManager.dto.feedto.GenerateFeesRequest;
import com.akhilesh.coachingManager.dto.feedto.GenerateFeesResponse;
import com.akhilesh.coachingManager.service.FeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/fees")
@RequiredArgsConstructor
public class FeeController {

    private final FeeService feeService;

    @PostMapping("/generate")
    public ResponseEntity<GenerateFeesResponse> generateFees(
            @Valid @RequestBody GenerateFeesRequest request) {
        return new ResponseEntity<>(feeService.generateFees(request), HttpStatus.CREATED);
    }

    @GetMapping("/batch/{batchId}")
    public ResponseEntity<List<FeeDueResponse>> getDuesByBatch(
            @PathVariable Long batchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        return ResponseEntity.ok(feeService.getDuesByBatchAndMonth(batchId, month));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<FeeDueResponse>> getDuesByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(feeService.getDuesByStudent(studentId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<FeeDueResponse>> getPendingDues() {
        return ResponseEntity.ok(feeService.getPendingDues());
    }
}
