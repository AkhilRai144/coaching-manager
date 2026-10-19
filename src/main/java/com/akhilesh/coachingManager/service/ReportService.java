package com.akhilesh.coachingManager.service;

import com.akhilesh.coachingManager.dto.AttendancePercent;
import com.akhilesh.coachingManager.dto.PendingFeeSummary;
import com.akhilesh.coachingManager.exception.ResourceNotFoundException;
import com.akhilesh.coachingManager.repository.BatchRepository;
import com.akhilesh.coachingManager.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final BatchRepository batchRepository;

    @Transactional(readOnly = true)
    public List<PendingFeeSummary> getPendingFeesByBatch() {
        return reportRepository.pendingFeesByBatch();
    }

    @Transactional(readOnly = true)
    public List<AttendancePercent> getAttendancePercent(Long batchId) {

        if (!batchRepository.existsById(batchId)) {
            throw new ResourceNotFoundException(
                    "Batch not found with id " + batchId
            );
        }

        return reportRepository.attendancePercentByBatch(batchId);
    }
}