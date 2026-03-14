package com.firstone.cv.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;

public class JwtUtilsTest {
    @Test
    public void testJwt() throws Exception {
        JwtUtils jwtUtils = new JwtUtils();
        java.lang.reflect.Field f1 = JwtUtils.class.getDeclaredField("secret");
        f1.setAccessible(true);
        f1.set(jwtUtils, "LLWl/MIy1WZzZp4SyK1lRARrAy+Fho/ctQm6rj6LQbU=");
        
        java.lang.reflect.Field f2 = JwtUtils.class.getDeclaredField("expiration");
        f2.setAccessible(true);
        f2.set(jwtUtils, 86400000L);
        
        UserDetails userDetails = new User("tamiratake@gmail.com", "password", new ArrayList<>());
        String token = jwtUtils.generateToken(userDetails);
        System.out.println("Generated token: " + token);
        
        String username = jwtUtils.extractUsername(token);
        System.out.println("Extracted username: " + username);
    }
}
