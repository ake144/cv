package com.firstone.cv.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_optimized_resumes")
@Data
public class UserOptimizedResume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String optimizedResume;

    private LocalDateTime updatedAt = LocalDateTime.now();
}
