package com.akhilesh.coachingManager.controller;

import com.akhilesh.coachingManager.dto.EnrollmentResponse;
import com.akhilesh.coachingManager.dto.FeeDueResponse;
import com.akhilesh.coachingManager.service.EnrollmentService;
import com.akhilesh.coachingManager.service.FeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final FeeService feeService;
    private final EnrollmentService enrollmentService;

    @GetMapping("/fees")
    public ResponseEntity<List<FeeDueResponse>> myFees(Principal principal) {
        return ResponseEntity.ok(feeService.getMyDues(principal.getName()));
    }

    @GetMapping("/enrollments")
    public ResponseEntity<List<EnrollmentResponse>> myEnrollments(Principal principal) {
        return ResponseEntity.ok(enrollmentService.getMyEnrollments(principal.getName()));
    }
}
