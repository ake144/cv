package com.firstone.cv.controller;

import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import com.firstone.cv.entity.SkillRoadMap;
import com.firstone.cv.entity.Resume;
import com.firstone.cv.entity.User;
import com.firstone.cv.entity.UserOptimizedResume;
import com.firstone.cv.repository.ResumeRepo;
import com.firstone.cv.repository.UserOptimizedResumeRepo;
import com.firstone.cv.repository.UserRepo;
import com.firstone.cv.dto.ResumeMetadata;
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
    private final UserRepo userRepo;
    private final UserOptimizedResumeRepo userOptimizedResumeRepo;
    private final JwtUtils jwtUtils;

    @PostMapping
    @Transactional
    public ResponseEntity<?> slay(@RequestBody ResumeRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Missing or invalid Authorization header");
            }

            System.out.println("token: " + authHeader + " | jobDesc: "
                    + request.getJobDescription() + " | jobTitle: " + request.getJobTitle() + " | jobUrl: "
                    + request.getJobUrl());

            String token = authHeader.substring(7).replaceAll("[\"\']", "").trim();
            String email = jwtUtils.extractUsername(token);

            User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

            // create a new slay history row for this optimization request
            Resume slay = new Resume();
            slay.setUser(user);
            slay.setJobTitle(request.getJobTitle() != null ? request.getJobTitle() : "Unknown Title");
            slay.setJobUrl(request.getJobUrl() != null ? request.getJobUrl() : "Unknown URL");
            slay.setAtsScore(request.getAtsScore() != null ? request.getAtsScore() : "92%");

            // Add the new fields
            if (request.getTrapsFixed() != null) {
                // If it's an array from JSON, it might come as a String representation if not
                // properly mapped, let's just save the string.
                slay.setTrapsFixed(request.getTrapsFixed().toString());
            }

            if (request.getRoadmap() != null) {
                SkillRoadMap roadmap = slay.getRoadMap();
                if (roadmap == null) {
                    roadmap = new SkillRoadMap();
                }
                roadmap.setResume(slay);
                roadmap.setRoadMapText(request.getRoadmap());
                if (request.getMissingSkills() != null) {
                    roadmap.setMissingSkills(request.getMissingSkills().toString());
                }
                slay.setRoadMap(roadmap);
            }

            slay = resumeRepo.save(slay);

            // update or create the canonical optimized resume for the user
            if (request.getOptimizedResume() != null && !request.getOptimizedResume().isBlank()) {
                UserOptimizedResume uor = userOptimizedResumeRepo.findByUser(user).orElse(null);
                if (uor == null) {
                    uor = new UserOptimizedResume();
                    uor.setUser(user);
                }
                uor.setOptimizedResume(request.getOptimizedResume());
                uor.setUpdatedAt(java.time.LocalDateTime.now());
                userOptimizedResumeRepo.save(uor);
            }

            String optimizedForResponse = userOptimizedResumeRepo.findByUser(user).map(UserOptimizedResume::getOptimizedResume).orElse(null);

            return ResponseEntity.ok(Map.of(
                    "optimizedResume", optimizedForResponse,
                    "atsScore", request.getAtsScore() != null ? request.getAtsScore() : "92%",
                    "slayId", slay.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @GetMapping
    public ResponseEntity<?> getSlays(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false, value = "page", defaultValue = "0") int page,
            @RequestParam(required = false, value = "size", defaultValue = "10") int size,
            @RequestParam(required = false, value = "sort", defaultValue = "createdAt,desc") String sort) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Missing or invalid Authorization header");
            }
            String token = authHeader.substring(7).replaceAll("[\"\']", "").trim();
            String email = jwtUtils.extractUsername(token);

            User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

            org.springframework.data.domain.Page<ResumeMetadata> pageResult = resumeRepo.findProjectedByUser(user,
                PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(sort.split(",")[1]), sort.split(",")[0])));

            Map<String, Object> response = new java.util.LinkedHashMap<>();
            response.put("currentPage", pageResult.getNumber());
            response.put("totalItems", pageResult.getTotalElements());
            response.put("totalPages", pageResult.getTotalPages());
            response.put("hasNext", pageResult.hasNext());

            java.util.List<Map<String, Object>> content = pageResult.getContent().stream().map(slay -> {
                Map<String, Object> map = new java.util.LinkedHashMap<>();
                map.put("id", slay.getId());
                map.put("jobTitle", slay.getJobTitle());
                map.put("jobUrl", slay.getJobUrl());
                map.put("atsScore", slay.getAtsScore());
                map.put("createdAt", slay.getCreatedAt().toString());
                return map;
            }).collect(java.util.stream.Collectors.toList());

            response.put("content", content);

            return ResponseEntity.ok(response);
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

            Map<String, Object> map = new java.util.LinkedHashMap<>();
            map.put("id", slay.getId());
            map.put("jobTitle", slay.getJobTitle());
            map.put("jobUrl", slay.getJobUrl());
            map.put("atsScore", slay.getAtsScore());
            // include the user's canonical optimized resume
            String optResume = userOptimizedResumeRepo.findByUser(user).map(UserOptimizedResume::getOptimizedResume).orElse(null);
            if (optResume != null) {
                map.put("optimizedResume", optResume.replace("\\n", "\n").replace("\\\"", "\""));
            }

            if (slay.getTrapsFixed() != null)
                map.put("trapsFixed", slay.getTrapsFixed().replace("\\n", "\n").replace("\\\"", "\""));
            if (slay.getRoadMap() != null)
                map.put("roadmap", new Object[] { slay.getRoadMap().getMissingSkills(), slay.getRoadMap().getRoadMapText() });
            map.put("createdAt", slay.getCreatedAt().toString());

            return ResponseEntity.ok(map);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

}

@Data
class ResumeRequest {
    private String jobDescription;
    private String jobTitle;
    private String jobUrl;
    private String optimizedResume;
    private String atsScore;
    private String trapsFixed;
    private Object missingSkills; // Array of strings or String
    private String roadmap;
}
