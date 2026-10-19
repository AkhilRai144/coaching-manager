package com.akhilesh.coachingManager.dto.batchdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
public class BatchResponse {

    private Long id;

    private String name;

    private LocalTime startTime;

    private LocalTime endTime;

    private BigDecimal monthlyFee;

    private Long teacherId;

    private String teacherName;
}
