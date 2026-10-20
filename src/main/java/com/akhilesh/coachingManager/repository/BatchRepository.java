package com.akhilesh.coachingManager.repository;

import com.akhilesh.coachingManager.entity.Batch;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface BatchRepository extends JpaRepository<Batch,Long> {

    @EntityGraph(attributePaths = "teacher")
    List<Batch> findAllBy();

    boolean existsByTeacherIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long teacherId, LocalTime endTime, LocalTime startTime);

    boolean existsByIdAndTeacherUserEmail(Long id, String email);
}
