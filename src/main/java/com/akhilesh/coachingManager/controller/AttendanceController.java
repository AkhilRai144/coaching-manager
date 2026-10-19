package com.akhilesh.coachingManager.controller;


import com.akhilesh.coachingManager.dto.attendancedto.AttendanceRequest;
import com.akhilesh.coachingManager.dto.attendancedto.AttendanceResponse;
import com.akhilesh.coachingManager.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<List<AttendanceResponse>> markAttendance(
            @Valid @RequestBody AttendanceRequest request) {
        List<AttendanceResponse> response = attendanceService.markAttendance(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/batch/{batchId}")
    public ResponseEntity<List<AttendanceResponse>> getAttendance(
            @PathVariable Long batchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getAttendance(batchId, date));
    }
}