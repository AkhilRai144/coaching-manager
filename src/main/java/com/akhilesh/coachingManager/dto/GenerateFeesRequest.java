package com.akhilesh.coachingManager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateFeesRequest {

    @NotNull(message = "Batch id is required")
    private Long batchId;
}