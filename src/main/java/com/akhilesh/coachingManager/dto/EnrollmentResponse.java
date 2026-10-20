package com.akhilesh.coachingManager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class EnrollmentResponse {

    private Long id;

    private Long studentId;

    private String studentName;

    private Long batchId;

    private String batchName;

    private LocalDate joinDate;
}
