package com.akhilesh.coachingManager.controller;

import com.akhilesh.coachingManager.dto.EnrollmentRequest;
import com.akhilesh.coachingManager.dto.EnrollmentResponse;
import com.akhilesh.coachingManager.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentResponse> enrollStudent(@Valid @RequestBody EnrollmentRequest request){
        EnrollmentResponse response = enrollmentService.enrollStudent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/batch/{batchId}")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentByBatch(@PathVariable Long batchId){
        return ResponseEntity.ok(enrollmentService.getEnrollmentByBatch(batchId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentByStudent(@PathVariable Long studentId){
        return ResponseEntity.ok(enrollmentService.getEnrollmentByStudent(studentId));
    }
}

