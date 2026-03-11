package com.firstone.cv.entity;

import org.checkerframework.checker.units.qual.C;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private String fullName;

    @Column
    private String role = "USER"; // Default role
}
