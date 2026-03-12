package com.firstone.cv.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firstone.cv.entity.Resume;
import com.firstone.cv.entity.User;
import com.firstone.cv.repository.ResumeRepo;
import com.firstone.cv.repository.UserRepo;
import com.firstone.cv.security.JwtUtils;
import com.firstone.cv.service.GrokService;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


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
    public ResponseEntity<?> slay(@RequestBody ResumeRequest request,
                   @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        String email = jwtUtils.extractUsername(token);

        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        String grokJson = grokService.optimizeResume(request.getResumeText(), request.getJobDescription());
        
        Resume slay = new Resume();
        slay.setUser(user);
        slay.setOriginalResume(request.getResumeText());
        slay.setOptimizedResume(grokJson);
        slay.setJobTitle(request.getJobTitle());
        slay.setJobUrl(request.getJobUrl());
        slay.setAtsScore("92%"); // Extract from grokJson in real implementation

        resumeRepo.save(slay);

        return ResponseEntity.ok(Map.of(
            "optimizedResume", grokJson,
            "atsScore", "92%",
            "slayId", slay.getId()
        ));


    }
    

}


@Data
class ResumeRequest {
    private String resumeText;
    private String jobDescription;
    private String jobTitle;
    private String jobUrl;
}
