package com.akhilesh.coachingManager.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PendingFeeSummary {

    private Long batchId;
    private String batchName;
    private int pendingDues;
    private BigDecimal pendingAmount;
}
