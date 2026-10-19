package com.akhilesh.coachingManager.service;

import com.akhilesh.coachingManager.dto.enrollmentdto.EnrollmentRequest;
import com.akhilesh.coachingManager.dto.enrollmentdto.EnrollmentResponse;
import com.akhilesh.coachingManager.entity.Batch;
import com.akhilesh.coachingManager.entity.Enrollment;
import com.akhilesh.coachingManager.entity.Student;
import com.akhilesh.coachingManager.exception.DuplicateResourceException;
import com.akhilesh.coachingManager.exception.ResourceNotFoundException;
import com.akhilesh.coachingManager.repository.BatchRepository;
import com.akhilesh.coachingManager.repository.EnrollmentRepository;
import com.akhilesh.coachingManager.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Setter
@RequiredArgsConstructor
@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final BatchRepository batchRepository;

    private EnrollmentResponse toResponse(Enrollment enrollment) {
        return new EnrollmentResponse(enrollment.getId()
                , enrollment.getStudent().getId()
                , enrollment.getStudent().getName()
                , enrollment.getBatch().getId()
                , enrollment.getBatch().getName()
                , enrollment.getJoinDate());
    }

    @Transactional
    public EnrollmentResponse enrollStudent(EnrollmentRequest request) {
        Student student = findActiveStudent(request.getStudentId());


//        if (student.isDeleted()) {
//            throw new ResourceNotFoundException("Student no found with id " + request.getStudentId());
//        }
        Batch batch = batchRepository.findById(request.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Batch not found with id " + request.getBatchId()
                ));

        if (enrollmentRepository.existsByStudentIdAndBatchId(student.getId(), batch.getId())) {
            throw new DuplicateResourceException("Student already enrolled in this batch");
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setBatch(batch);
        enrollment.setJoinDate(LocalDate.now());

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return toResponse(savedEnrollment);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentByBatch(Long batchId) {
        if (!batchRepository.existsById(batchId)){
            throw new ResourceNotFoundException("Batch not found with id "+ batchId);
        }
        return enrollmentRepository.findByBatchId(batchId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentByStudent(Long studentId) {
        findActiveStudent(studentId);
             return enrollmentRepository.findByStudentId(studentId)
                .stream()
                .map(this::toResponse)
                .toList();

    }

    private Student findActiveStudent(Long id){
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " +id));

        if(student.isDeleted()){
            throw new ResourceNotFoundException("Student not found with id " +id);
        }
        return student;
    }

}