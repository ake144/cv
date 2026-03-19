package com.firstone.cv.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Id;

// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "resumes")
@Data
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String originalResume;

    @Column(columnDefinition = "TEXT")
    private String optimizedResume;

    private String jobTitle;
    private String jobUrl;
;    @Column(columnDefinition = "TEXT")
    private String atsScore;

    @Column(columnDefinition = "TEXT")
    private String trapsFixed;

    @OneToOne(mappedBy = "resume", cascade = CascadeType.ALL)
    private SkillRoadMap roadMap;

    private LocalDate createdAt = LocalDate.now();
    
}
