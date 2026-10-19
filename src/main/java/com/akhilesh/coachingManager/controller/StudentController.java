package com.akhilesh.coachingManager.controller;

import com.akhilesh.coachingManager.dto.studentdto.StudentRequest;
import com.akhilesh.coachingManager.dto.studentdto.StudentResponse;
import com.akhilesh.coachingManager.dto.studentdto.StudentUpdateRequest;
import com.akhilesh.coachingManager.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentResponse> addStudent(  @Valid @RequestBody StudentRequest studentRequest){
        StudentResponse response = studentService.addStudent(studentRequest);

        return  new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents(){
        List<StudentResponse> list = studentService.getAllStudents();
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id){
        StudentResponse studentResponse = studentService.getStudentById(id);
        return new ResponseEntity<>(studentResponse,HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long id
                          ,@RequestBody StudentUpdateRequest request){
        StudentResponse updatedStudent = studentService.updateStudent(id,request);
        return new ResponseEntity<>(updatedStudent,HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id){
        studentService.deleteStudent(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
