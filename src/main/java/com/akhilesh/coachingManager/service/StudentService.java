package com.akhilesh.coachingManager.service;

import com.akhilesh.coachingManager.audit.Audited;
import com.akhilesh.coachingManager.dto.StudentRequest;
import com.akhilesh.coachingManager.dto.StudentResponse;
import com.akhilesh.coachingManager.dto.StudentUpdateRequest;
import com.akhilesh.coachingManager.entity.Role;
import com.akhilesh.coachingManager.entity.Student;
import com.akhilesh.coachingManager.entity.User;
import com.akhilesh.coachingManager.exception.DuplicateResourceException;
import com.akhilesh.coachingManager.exception.ResourceNotFoundException;
import com.akhilesh.coachingManager.repository.StudentRepository;
import com.akhilesh.coachingManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public StudentResponse addStudent(StudentRequest studentRequest){
       if(userRepository.existsByEmail(studentRequest.getEmail())){
           throw new DuplicateResourceException("Email already registered");
        }
        User user = new User();
       user.setEmail(studentRequest.getEmail());
       user.setPassword(passwordEncoder.encode(studentRequest.getPassword()));
       user.setRole(Role.STUDENT);
       User savedUser = userRepository.save(user);

        Student student = new Student();
        student.setName(studentRequest.getName());
        student.setName(studentRequest.getName());
        student.setPhone(studentRequest.getPhone());
        student.setUser(savedUser);
        Student savedStudent = studentRepository.save(student);

        return toResponse(savedStudent);
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents(){
       return studentRepository.findByDeletedFalse()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id){
        Student student = findActiveStudent(id);
        return toResponse(student);
    }

    @Transactional
    public StudentResponse updateStudent(Long id, StudentUpdateRequest request){
        Student student = findActiveStudent(id);
        student.setName(request.getName());
        student.setPhone(request.getPhone());
       Student updatedStudent = studentRepository.save(student);

        return toResponse(updatedStudent);
    }

    @Transactional
    @Audited("STUDENT_DELETED")
    public void deleteStudent(Long id){
        Student student = findActiveStudent(id);
        student.setDeleted(true);
        studentRepository.save(student);
    }





         //  <---------Helper Methods --------->

    private Student findActiveStudent(Long id){
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " +id));

        if(student.isDeleted()){
            throw new ResourceNotFoundException("Student not found with id " +id);
        }
        return student;
    }

    private StudentResponse toResponse(Student student){
        return new StudentResponse(student.getId()
                ,student.getName()
                ,student.getPhone()
                ,student.getUser().getEmail());

    }
}
