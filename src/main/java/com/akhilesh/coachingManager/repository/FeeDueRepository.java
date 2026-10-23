package com.akhilesh.coachingManager.repository;

import com.akhilesh.coachingManager.entity.FeeDue;
import com.akhilesh.coachingManager.entity.FeeStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FeeDueRepository extends JpaRepository<FeeDue, Long> {

    @EntityGraph(attributePaths = {"enrollment.student", "enrollment.batch"})
    List<FeeDue> findByEnrollmentBatchIdOrderByPeriodStartDesc(Long batchId);

    @EntityGraph(attributePaths = {"enrollment.student", "enrollment.batch"})
    List<FeeDue> findByEnrollmentStudentId(Long studentId);

    @EntityGraph(attributePaths = {"enrollment.student", "enrollment.batch"})
    List<FeeDue> findByStatusAndEnrollmentStudentDeletedFalse(FeeStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from FeeDue f where f.id = :id")
    Optional<FeeDue> findByIdForUpdate(@Param("id") Long id);
}