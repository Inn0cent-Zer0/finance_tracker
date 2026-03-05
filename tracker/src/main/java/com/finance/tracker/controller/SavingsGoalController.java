package com.finance.tracker.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finance.tracker.dto.SavingsGoalRequest;
import com.finance.tracker.dto.SavingsGoalResponse;
import com.finance.tracker.service.SavingsGoalService;

@RestController
@RequestMapping("/api/savings-goals")
@CrossOrigin(origins = "*")
public class SavingsGoalController {

    @Autowired private SavingsGoalService savingsGoalService;

    @PostMapping
    public ResponseEntity<?> create(@AuthenticationPrincipal UserDetails userDetails,
                                    @RequestBody SavingsGoalRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(savingsGoalService.create(userDetails.getUsername(), request));
        } catch (Exception e) {
            System.err.println("[GOAL CREATE ERROR] " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<SavingsGoalResponse>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(savingsGoalService.getAll(userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@AuthenticationPrincipal UserDetails userDetails,
                                    @PathVariable Long id,
                                    @RequestBody SavingsGoalRequest request) {
        try {
            return ResponseEntity.ok(savingsGoalService.update(userDetails.getUsername(), id, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal UserDetails userDetails,
                                    @PathVariable Long id) {
        try {
            savingsGoalService.delete(userDetails.getUsername(), id);
            return ResponseEntity.ok(Map.of("message", "Goal deleted."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
