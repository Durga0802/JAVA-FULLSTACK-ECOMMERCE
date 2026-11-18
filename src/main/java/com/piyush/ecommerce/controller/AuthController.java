package com.piyush.ecommerce.controller;

import com.piyush.ecommerce.dto.LoginRequest;
import com.piyush.ecommerce.security.JwtUtil;
import com.piyush.ecommerce.service.UserService; // Isse import karna zaroori hai
import com.piyush.ecommerce.entity.User; // Apne Entity wale User ko import karo
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService; // 1. UserService inject kiya

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserService userService) { // 2. Constructor mein add kiya
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String role = userDetails.getAuthorities().stream()
                .map(r -> r.getAuthority())
                .findFirst()
                .orElse("USER");

        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role.toUpperCase();
        }

        String token = jwtUtil.generateToken(userDetails.getUsername(), role);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return response;
    }

    // 3. Admin registration ka sahi logic
    @PostMapping("/register-admin-secret")
    public String registerAdmin() {
        // Purana user delete karne ki zaroorat nahi, ye naya email hai
        User admin = new User();
        admin.setName("Piyush Admin");
        admin.setEmail("piyush@test.com");
        admin.setPassword("admin123"); // UserService ise khud BCrypt karegi
        admin.setRole("ADMIN");

        userService.createUser(admin);
        return "Admin created successfully! Ab piyush@test.com aur password 'admin123' se login karo.";
    }
}