package com.akhilesh.coachingManager.controller;

import com.akhilesh.coachingManager.dto.AttendancePercent;
import com.akhilesh.coachingManager.dto.PendingFeeSummary;
import com.akhilesh.coachingManager.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/pending-fees")
    public ResponseEntity<List<PendingFeeSummary>> getPendingFeesByBatch() {
        return ResponseEntity.ok(
                reportService.getPendingFeesByBatch()
        );
    }

    @GetMapping("/attendance/batch/{batchId}")
    public ResponseEntity<List<AttendancePercent>> getAttendancePercent(
            @PathVariable Long batchId
    ) {
        return ResponseEntity.ok(
                reportService.getAttendancePercent(batchId)
        );
    }
}
