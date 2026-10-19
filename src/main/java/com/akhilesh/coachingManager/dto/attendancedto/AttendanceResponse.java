package com.akhilesh.coachingManager.dto.attendancedto;


import com.akhilesh.coachingManager.entity.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class AttendanceResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long batchId;
    private String batchName;
    private LocalDate attendanceDate;
    private AttendanceStatus status;
}
