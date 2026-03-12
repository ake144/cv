package com.firstone.cv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.firstone.cv.entity.Resume;

public interface ResumeRepo extends JpaRepository<Resume, Long> {
    List<Resume> findByUserId(Long userId);
    
}


