package com.firstone.cv.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import com.firstone.cv.dto.ResumeMetadata;
import com.firstone.cv.entity.Resume;
import com.firstone.cv.entity.User;

public interface ResumeRepo extends JpaRepository<Resume, Long> {
    List<Resume> findByUserId(Long userId);

    List<Resume> findAllByUserOrderByCreatedAtDesc(User user);

    Page<Resume> findByUser(User user, Pageable pageable);

    @Query("""
            select r.id as id,
                   r.jobTitle as jobTitle,
                   r.jobUrl as jobUrl,
                   r.atsScore as atsScore,
                   r.createdAt as createdAt
            from Resume r
            where r.user = :user
            """)
    Page<ResumeMetadata> findProjectedByUser(User user, Pageable pageable);
}
