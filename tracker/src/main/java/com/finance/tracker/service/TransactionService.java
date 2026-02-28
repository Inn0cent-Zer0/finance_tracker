package com.finance.tracker.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance.tracker.dto.TransactionRequest;
import com.finance.tracker.dto.TransactionResponse;
import com.finance.tracker.model.Transaction;
import com.finance.tracker.model.TransactionType;
import com.finance.tracker.model.User;
import com.finance.tracker.repository.TransactionRepository;
import com.finance.tracker.repository.UserRepository;

@Service
public class TransactionService {

    @Autowired private TransactionRepository transactionRepository;
    @Autowired private UserRepository userRepository;

    // Helper — load user by email (from JWT principal)
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    // ── CREATE ────────────────────────────────────────────────────────────────
    @Transactional
    public TransactionResponse create(String email, TransactionRequest req) {
        User user = getUser(email);

        Transaction t = new Transaction();
        t.setUser(user);
        t.setType(req.getType());
        t.setCategory(req.getCategory());
        t.setAmount(req.getAmount());
        t.setDescription(req.getDescription());
        t.setDate(req.getDate());

        return TransactionResponse.from(transactionRepository.saveAndFlush(t));
    }

    // ── READ ALL ──────────────────────────────────────────────────────────────
    public List<TransactionResponse> getAll(String email) {
        User user = getUser(email);
        return transactionRepository.findByUserId(user.getId())
                .stream().map(TransactionResponse::from).collect(Collectors.toList());
    }

    // ── READ ONE ──────────────────────────────────────────────────────────────
    public TransactionResponse getOne(String email, Long id) {
        Transaction t = findAndVerifyOwner(email, id);
        return TransactionResponse.from(t);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    @Transactional
    public TransactionResponse update(String email, Long id, TransactionRequest req) {
        Transaction t = findAndVerifyOwner(email, id);

        t.setType(req.getType());
        t.setCategory(req.getCategory());
        t.setAmount(req.getAmount());
        t.setDescription(req.getDescription());
        t.setDate(req.getDate());

        return TransactionResponse.from(transactionRepository.saveAndFlush(t));
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    @Transactional
    public void delete(String email, Long id) {
        Transaction t = findAndVerifyOwner(email, id);
        transactionRepository.delete(t);
    }

    // ── SUMMARY (total income, total expense, balance) ────────────────────────
    public java.util.Map<String, Double> getSummary(String email) {
        User user = getUser(email);
        Double totalIncome  = transactionRepository.sumByUserIdAndType(user.getId(), TransactionType.INCOME);
        Double totalExpense = transactionRepository.sumByUserIdAndType(user.getId(), TransactionType.EXPENSE);
        Double balance      = totalIncome - totalExpense;
        return java.util.Map.of(
            "totalIncome",  totalIncome,
            "totalExpense", totalExpense,
            "balance",      balance
        );
    }

    // ── GUARD: ensure transaction belongs to this user ─────────────────────────
    private Transaction findAndVerifyOwner(String email, Long id) {
        Transaction t = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + id));
        if (!t.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Access denied.");
        }
        return t;
    }
}
