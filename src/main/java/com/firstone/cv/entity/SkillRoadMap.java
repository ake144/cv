package com.firstone.cv.entity;


import jakarta.persistence.*;



public class SkillRoadMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @Column(columnDefinition = "TEXT")
    private String missingSkills;


    @Column(columnDefinition = "TEXT")
    private String roadMapText;
}
