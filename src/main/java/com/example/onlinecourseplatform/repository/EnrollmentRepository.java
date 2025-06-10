package com.example.onlinecourseplatform.repository;

import com.example.onlinecourseplatform.entity.Enrollment;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long userId);

    boolean existsByStudentIdAndCourseId(Long userId, Long courseId);

    void deleteByStudentIdAndCourseId(Long userId, @NotNull Long courseId);

    void deleteByCourseId(Long courseId);

    void deleteByStudentId(Long studentID);

    List<Enrollment> findByCourseId(Long courseId);
}

