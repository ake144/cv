package com.firstone.cv.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firstone.cv.repository.ResumeRepo;
import com.firstone.cv.repository.UserRepo;
import com.firstone.cv.security.JwtUtils;
import com.firstone.cv.service.GrokService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/slayer")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ResumeSlayerController {
    
    private final GrokService grokService;
    private final ResumeRepo resumeRepo;
    private final UserRepo   userRepo;
    private final JwtUtils jwtUtils;


    @PostMapping
    public String postMethodName(@RequestBody String entity) {
        //TODO: process POST request
        
        return entity;
    }
    

}
