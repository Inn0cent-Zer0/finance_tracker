package com.finance.tracker.controller;

import com.finance.tracker.dto.BudgetRequest;
import com.finance.tracker.dto.BudgetResponse;
import com.finance.tracker.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budgets")
@CrossOrigin(origins = "*")
public class BudgetController {

    @Autowired private BudgetService budgetService;

    @PostMapping
    public ResponseEntity<?> setBudget(@AuthenticationPrincipal UserDetails userDetails,
                                       @RequestBody BudgetRequest request) {
        try {
            BudgetResponse res = budgetService.setbudget(userDetails.getUsername(), request);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            System.err.println("[BUDGET SET ERROR] " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getBudgets(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Integer month,
            @RequestParam Integer year) {
        return ResponseEntity.ok(budgetService.getBudgets(userDetails.getUsername(), month, year));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(@AuthenticationPrincipal UserDetails userDetails,
                                          @PathVariable Long id) {
        try {
            budgetService.deleteBudget(userDetails.getUsername(), id);
            return ResponseEntity.ok(Map.of("message", "Budget deleted."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
