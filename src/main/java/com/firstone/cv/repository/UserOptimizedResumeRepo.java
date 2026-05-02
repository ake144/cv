package com.firstone.cv.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.firstone.cv.entity.User;
import com.firstone.cv.entity.UserOptimizedResume;

public interface UserOptimizedResumeRepo extends JpaRepository<UserOptimizedResume, Long> {
    Optional<UserOptimizedResume> findByUser(User user);
}
