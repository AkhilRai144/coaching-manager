package com.akhilesh.coachingManager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EnrollmentRequest {
    @NotNull(message = "Student id required")
    private Long studentId;

    @NotNull(message = "Batch id required")
    private Long batchId;
}
