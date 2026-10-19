package com.akhilesh.coachingManager.controller;

import com.akhilesh.coachingManager.dto.teacherdto.TeacherRequest;
import com.akhilesh.coachingManager.dto.teacherdto.TeacherResponse;
import com.akhilesh.coachingManager.dto.teacherdto.TeacherUpdateRequest;
import com.akhilesh.coachingManager.dto.teacherdto.TeacherUpdateResponse;
import com.akhilesh.coachingManager.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    public ResponseEntity<TeacherResponse> addTeacher(@Valid @RequestBody TeacherRequest request){
        TeacherResponse response = teacherService.addTeacher(request);

        return  new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TeacherResponse>> getAllTeacher(){
        List<TeacherResponse> list = teacherService.getAllTeacher();
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponse> getStudentById(@PathVariable Long id){
        TeacherResponse teacherResponse = teacherService.getTeacherById(id);
        return new ResponseEntity<>(teacherResponse,HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherUpdateResponse> updateTeacher(@PathVariable Long id
            ,@RequestBody TeacherUpdateRequest request){
        TeacherUpdateResponse updateTeacher =  teacherService.updateTeacher(id,request);
        return new ResponseEntity<>(updateTeacher,HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id){
        teacherService.deleteTeacher(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

