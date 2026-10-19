package com.akhilesh.coachingManager.dto.attendancedto;

import com.akhilesh.coachingManager.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceEntry {

    @NotNull(message = "Student id is required")
    private Long studentId;

    @NotNull(message = "Status is required")
    private AttendanceStatus status;
}