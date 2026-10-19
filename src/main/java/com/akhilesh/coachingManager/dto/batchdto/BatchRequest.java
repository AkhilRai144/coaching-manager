package com.akhilesh.coachingManager.dto.batchdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
public class BatchRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Start timing is required")
    private LocalTime startTime;

    @NotNull(message = "End timing is required")
    private LocalTime endTime;

    @NotNull(message = "Monthly Fee is required")
    @Positive
    private BigDecimal monthlyFee;

    @NotNull(message = "Teacher id is required")
    private Long teacherId;
}
