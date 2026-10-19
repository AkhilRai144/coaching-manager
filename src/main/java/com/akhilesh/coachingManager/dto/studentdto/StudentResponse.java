package com.akhilesh.coachingManager.dto.studentdto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StudentResponse {

    private Long id;

    private String name;

    private String phone;

    private String email;

}
