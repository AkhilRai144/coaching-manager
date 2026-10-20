package com.akhilesh.coachingManager.repository;

import com.akhilesh.coachingManager.entity.Teacher;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher,Long> {

    @EntityGraph(attributePaths = "user")
    List<Teacher> findByDeletedFalse();

    boolean existsByUserIdAndDeletedTrue(Long userId);
}
