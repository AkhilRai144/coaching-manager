package com.akhilesh.coachingManager.dto.feedto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class GenerateFeesRequest {

    @NotNull(message = "Batch id is required")
    private Long batchId;

    @NotNull(message = "Fee month is required")
    private LocalDate feeMonth;
}