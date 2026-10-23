package com.akhilesh.coachingManager.controller;

import com.akhilesh.coachingManager.dto.FeeDueResponse;
import com.akhilesh.coachingManager.dto.GenerateFeesRequest;
import com.akhilesh.coachingManager.dto.GenerateFeesResponse;
import com.akhilesh.coachingManager.service.FeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<FeeDueResponse>> getDuesByBatch(@PathVariable Long batchId) {
        return ResponseEntity.ok(feeService.getDuesByBatch(batchId));
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