package com.akhilesh.coachingManager.service;

import com.akhilesh.coachingManager.audit.Audited;
import com.akhilesh.coachingManager.dto.TeacherRequest;
import com.akhilesh.coachingManager.dto.TeacherResponse;
import com.akhilesh.coachingManager.dto.TeacherUpdateRequest;
import com.akhilesh.coachingManager.dto.TeacherUpdateResponse;
import com.akhilesh.coachingManager.entity.Role;
import com.akhilesh.coachingManager.entity.Teacher;
import com.akhilesh.coachingManager.entity.User;
import com.akhilesh.coachingManager.exception.DuplicateResourceException;
import com.akhilesh.coachingManager.exception.ResourceNotFoundException;
import com.akhilesh.coachingManager.repository.TeacherRepository;
import com.akhilesh.coachingManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public TeacherResponse addTeacher(TeacherRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException("Email already registered");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.TEACHER);
        User savedUser = userRepository.save(user);

        Teacher teacher= new Teacher();
        teacher.setName(request.getName());
        teacher.setSubject(request.getSubject());
        teacher.setUser(savedUser);
        Teacher savedTeacher = teacherRepository.save(teacher);

        return toResponse(savedTeacher);
    }

    @Transactional(readOnly = true)
    public List<TeacherResponse> getAllTeacher(){
        return teacherRepository.findByDeletedFalse()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TeacherResponse getTeacherById(Long id){
        Teacher teacher = findActiveTeacher(id);
        return toResponse(teacher);
    }

    @Transactional
    public TeacherUpdateResponse updateTeacher(Long id, TeacherUpdateRequest request){
        Teacher teacher = findActiveTeacher(id);
        teacher.setName(request.getName());
        teacher.setSubject(request.getSubject());
        Teacher updatedTeacher= teacherRepository.save(teacher);

        return new TeacherUpdateResponse(updatedTeacher.getName(),updatedTeacher.getSubject());
    }

    @Transactional
    @Audited("TEACHER_DELETED")
    public void deleteTeacher(Long id){
        Teacher teacher = findActiveTeacher(id);
        teacher.setDeleted(true);
        teacherRepository.save(teacher);
    }





    private Teacher findActiveTeacher(Long id){
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id " +id));

        if(teacher.isDeleted()){
            throw new ResourceNotFoundException("Teacher not found with id " +id);
        }
        return teacher;
    }

    private TeacherResponse toResponse(Teacher teacher){
        return new TeacherResponse(teacher.getId()
                ,teacher.getName()
                ,teacher.getSubject()
                ,teacher.getUser().getEmail());

}


}
