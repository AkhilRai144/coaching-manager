package com.akhilesh.coachingManager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GenerateFeesResponse {

    private int created;
    private int alreadyExisted;
}