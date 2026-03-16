package com.firstone.cv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.firstone.cv.entity.Resume;
import com.firstone.cv.entity.User;

public interface ResumeRepo extends JpaRepository<Resume, Long> {
    List<Resume> findByUserId(Long userId);

    List<Resume> findByUser(User user);

    
}


