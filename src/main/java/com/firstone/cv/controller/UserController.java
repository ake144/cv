package com.firstone.cv.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firstone.cv.entity.User;
import com.firstone.cv.repository.UserRepo;
import com.firstone.cv.security.JwtUtils;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/user")
public class UserController {
     private final UserRepo userRepo;
     private final JwtUtils jwtUtils;

    //  public UserController(UserRepo userRepo, JwtUtils jwtUtils) {
    //      this.userRepo = userRepo;
    //      this.jwtUtils = jwtUtils;
    //  }


     @GetMapping
     public ResponseEntity<?> getUserInfo(
        @RequestHeader (value = "Authorization", required = true) String authHeader)
      {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Missing or invalid Authorization header");
            }
            String token = authHeader.substring(7).replaceAll("[\"\']", "").trim();
            String email = jwtUtils.extractUsername(token);

            User user  =  userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("user not found"));
           
            return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "name", user.getFullName(),
                "role", user.getRole()
            ));
      }
}
