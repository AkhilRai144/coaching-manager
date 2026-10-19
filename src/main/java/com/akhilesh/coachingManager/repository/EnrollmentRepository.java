package com.akhilesh.coachingManager.repository;

import com.akhilesh.coachingManager.entity.Enrollment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment,Long> {

    boolean existsByStudentIdAndBatchId(Long studentId, Long batchId);

    @EntityGraph(attributePaths = {"student","batch"})
    List<Enrollment> findByBatchId(Long batchId);

    @EntityGraph(attributePaths = {"student","batch"})
    List<Enrollment> findByStudentId(Long studentId);
}
