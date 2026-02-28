package com.finance.tracker.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.finance.tracker.dto.AuthResponse;
import com.finance.tracker.dto.LoginRequest;
import com.finance.tracker.dto.RegisterRequest; // <--- added
import com.finance.tracker.model.Role;
import com.finance.tracker.model.User;
import com.finance.tracker.repository.RoleRepository;
import com.finance.tracker.repository.UserRepository;
import com.finance.tracker.security.JwtUtil;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtil jwtUtil;

    // ── REGISTER ─────────────────────────────────────────────────────────────
    public AuthResponse register(RegisterRequest request) {

        // Check duplicates
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use.");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken.");
        }

        // Get or create ROLE_USER
        Role userRole = roleRepository.findByRolename("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_USER")));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        // Build and save the user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIncome(request.getIncome());
        user.setSavings(request.getSavings());
        user.setTargetExpenses(request.getTargetExpenses());
        user.setRoles(roles);

        userRepository.save(user);

        // Generate token
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getUsername(), user.getEmail(), "ROLE_USER");
    }

    // ── LOGIN ─────────────────────────────────────────────────────────────────
    public AuthResponse login(LoginRequest request) {

        // authenticate and capture the Authentication object
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        // use it – set it in the security context
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Load user to build response
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found."));

        String role = user.getRoles().stream()
                .findFirst()
                .map(Role::getRolename)
                .orElse("ROLE_USER");

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getUsername(), user.getEmail(), role);
    }
}