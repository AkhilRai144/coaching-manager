package com.akhilesh.coachingManager.dto.feedto;

import com.akhilesh.coachingManager.entity.FeeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class FeeDueResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long batchId;
    private String batchName;
    private LocalDate feeMonth;
    private BigDecimal amount;
    private FeeStatus status;
}
