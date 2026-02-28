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

import com.finance.tracker.dto.TransactionRequest;
import com.finance.tracker.dto.TransactionResponse;
import com.finance.tracker.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // POST /api/transactions — add new transaction
    @PostMapping
    public ResponseEntity<?> create(@AuthenticationPrincipal UserDetails userDetails,
                                    @RequestBody TransactionRequest request) {
        try {
            TransactionResponse response = transactionService.create(userDetails.getUsername(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.err.println("[TX CREATE ERROR] " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/transactions — get all transactions for logged-in user
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(transactionService.getAll(userDetails.getUsername()));
    }

    // GET /api/transactions/{id} — get one transaction
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@AuthenticationPrincipal UserDetails userDetails,
                                    @PathVariable Long id) {
        try {
            return ResponseEntity.ok(transactionService.getOne(userDetails.getUsername(), id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // PUT /api/transactions/{id} — update a transaction
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@AuthenticationPrincipal UserDetails userDetails,
                                    @PathVariable Long id,
                                    @RequestBody TransactionRequest request) {
        try {
            return ResponseEntity.ok(transactionService.update(userDetails.getUsername(), id, request));
        } catch (Exception e) {
            System.err.println("[TX UPDATE ERROR] " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE /api/transactions/{id} — delete a transaction
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal UserDetails userDetails,
                                    @PathVariable Long id) {
        try {
            transactionService.delete(userDetails.getUsername(), id);
            return ResponseEntity.ok(Map.of("message", "Transaction deleted."));
        } catch (Exception e) {
            System.err.println("[TX DELETE ERROR] " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/transactions/summary — total income, expense, balance
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Double>> getSummary(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(transactionService.getSummary(userDetails.getUsername()));
    }
}
