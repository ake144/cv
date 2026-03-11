package com.firstone.cv.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.firstone.cv.entity.User;
import com.firstone.cv.repository.UserRepo;
import com.firstone.cv.security.JwtUtils;

@Service
public class AuthService {
    
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;


    public AuthService(UserRepo userRepo, PasswordEncoder passwordEncoder, JwtUtils jwtUtils,
        AuthenticationManager authenticationManager) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }


    public String register(String email, String password, String  fullName, String role) {
      if(userRepo.findByEmail(email).isPresent()) {
        throw new RuntimeException("User already exists");
      }

      User user = new User();
      user.setEmail(email);
      user.setPassword(passwordEncoder.encode(password));
      user.setFullName(fullName);
      user.setRole(role);  

       userRepo.save(user);
    return jwtUtils.generateToken(
        org.springframework.security.core.userdetails.User.builder()
        .username(email)
        .password(password)
        .roles(role)
        .build());
    }

    public String login(String email, String password){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return jwtUtils.generateToken(
            org.springframework.security.core.userdetails.User.builder()
            .username(email)
            .password(password)
            .roles(userRepo.findByEmail(email).orElseThrow().getRole())
            .build()
        );
    } 

}
