package com.akhilesh.coachingManager.controller;

import com.akhilesh.coachingManager.dto.AttendanceRequest;
import com.akhilesh.coachingManager.dto.AttendanceResponse;
import com.akhilesh.coachingManager.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<List<AttendanceResponse>> markAttendance(
            @Valid @RequestBody AttendanceRequest request,
            Authentication authentication) {

        List<AttendanceResponse> response = attendanceService.markAttendance(
                request, authentication.getName(), isOwner(authentication));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/batch/{batchId}")
    public ResponseEntity<List<AttendanceResponse>> getAttendance(
            @PathVariable Long batchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {

        return ResponseEntity.ok(attendanceService.getAttendance(
                batchId, date, authentication.getName(), isOwner(authentication)));
    }

    private boolean isOwner(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_OWNER"));
    }
}