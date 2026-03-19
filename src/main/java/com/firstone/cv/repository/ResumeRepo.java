package com.firstone.cv.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.firstone.cv.entity.Resume;
import com.firstone.cv.entity.User;

public interface ResumeRepo extends JpaRepository<Resume, Long> {
    List<Resume> findByUserId(Long userId);

    Page<Resume> findByUser(User user, Pageable pageable);

    
}


