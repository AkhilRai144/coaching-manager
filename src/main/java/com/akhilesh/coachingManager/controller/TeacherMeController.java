package com.akhilesh.coachingManager.controller;

import com.akhilesh.coachingManager.dto.BatchResponse;
import com.akhilesh.coachingManager.dto.EnrollmentResponse;
import com.akhilesh.coachingManager.service.BatchService;
import com.akhilesh.coachingManager.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/me/batches")
@RequiredArgsConstructor
public class TeacherMeController {

    private final BatchService batchService;
    private final EnrollmentService enrollmentService;

    @GetMapping
    public ResponseEntity<List<BatchResponse>> myBatches(Principal principal) {
        return ResponseEntity.ok(batchService.getMyBatches(principal.getName()));
    }

    @GetMapping("/{batchId}/students")
    public ResponseEntity<List<EnrollmentResponse>> myBatchStudents(
            @PathVariable Long batchId, Principal principal) {
        return ResponseEntity.ok(
                enrollmentService.getMyBatchStudents(batchId, principal.getName()));
    }
}