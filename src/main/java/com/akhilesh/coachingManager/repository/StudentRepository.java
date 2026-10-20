package com.akhilesh.coachingManager.repository;

import com.akhilesh.coachingManager.entity.Student;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student,Long> {

    @EntityGraph(attributePaths = "user")
    List<Student> findByDeletedFalse();


    boolean existsByUserIdAndDeletedTrue(Long userId);

    Optional<Student> findByUserEmail(String email);
}
