package com.akhilesh.coachingManager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TeacherResponse {

    private Long id;

    private String name;

    private String subject;

    private String  email;

}
