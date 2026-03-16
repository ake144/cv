package com.firstone.cv.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firstone.cv.entity.Resume;
import com.firstone.cv.entity.User;
import com.firstone.cv.repository.ResumeRepo;
import com.firstone.cv.repository.UserRepo;
import com.firstone.cv.security.JwtUtils;

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
    
    private final ResumeRepo resumeRepo;
    private final UserRepo   userRepo;
    private final JwtUtils jwtUtils;


    @PostMapping
    public ResponseEntity<?> slay(@RequestBody ResumeRequest request,
                   @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Missing or invalid Authorization header");
            }

            System.out.println( "token: " + authHeader + " | resume: " + request.getResumeText() + " | jobDesc: " + request.getJobDescription() + " | jobTitle: " + request.getJobTitle() + " | jobUrl: " + request.getJobUrl());

            String token = authHeader.substring(7).replaceAll("[\"\']", "").trim();
            String email = jwtUtils.extractUsername(token);

            User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            
            Resume slay = new Resume();
            slay.setUser(user);
            slay.setOriginalResume(request.getResumeText());
            slay.setOptimizedResume(request.getOptimizedResume());
            slay.setJobTitle(request.getJobTitle() != null ? request.getJobTitle() : "Unknown Title");
            slay.setJobUrl(request.getJobUrl() != null ? request.getJobUrl() : "Unknown URL");
            slay.setAtsScore(request.getAtsScore() != null ? request.getAtsScore() : "92%");

            resumeRepo.save(slay);

            return ResponseEntity.ok(Map.of(
                "optimizedResume", request.getOptimizedResume(),
                "atsScore", request.getAtsScore() != null ? request.getAtsScore() : "92%",
                "slayId", slay.getId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @GetMapping
    public ResponseEntity<?> getSlays(
        @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Missing or invalid Authorization header");
            }
            String token = authHeader.substring(7).replaceAll("[\"\']", "").trim();
            String email = jwtUtils.extractUsername(token);

            User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(resumeRepo.findByUser(user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @GetMapping("/{slayId}")
    public ResponseEntity<?> getSlayById(
        @RequestHeader(value = "Authorization", required = false) String authHeader,
        @PathVariable Long slayId) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Missing or invalid Authorization header");
            }
            String token = authHeader.substring(7).replaceAll("[\"\']", "").trim();
            String email = jwtUtils.extractUsername(token);

            User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

            Resume slay = resumeRepo.findById(slayId).orElseThrow(() -> new RuntimeException("Slay not found"));
            if (!slay.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("Forbidden: You do not have access to this slay");
            }
            return ResponseEntity.ok(slay);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

}


@Data
class ResumeRequest {
    private String resumeText;
    private String jobDescription;
    private String jobTitle;
    private String jobUrl;
    private String optimizedResume;
    private String atsScore;
}
