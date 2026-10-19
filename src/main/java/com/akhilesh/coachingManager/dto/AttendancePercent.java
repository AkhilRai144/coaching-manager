package com.akhilesh.coachingManager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class AttendancePercent {

    private Long studentId;
    private String studentName;
    private int totalClasses;
    private int presentCount;
    private BigDecimal percentage;
}