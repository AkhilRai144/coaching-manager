package com.akhilesh.coachingManager.dto.feedto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GenerateFeesResponse {

    private int created;
    private int alreadyExisted;
}
