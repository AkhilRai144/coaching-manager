package com.akhilesh.coachingManager.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class AttendanceRequest {

    @NotNull(message = "Batch id is required")
    private Long batchId;

    @NotNull(message = "Attendance date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate attendanceDate;

    @NotEmpty(message = "At least one attendance entry is required")
    @Valid
    private List<AttendanceEntry> entries;
}
