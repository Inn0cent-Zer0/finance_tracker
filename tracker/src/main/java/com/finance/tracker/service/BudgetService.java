package com.finance.tracker.service;

import com.finance.tracker.dto.BudgetRequest;
import com.finance.tracker.dto.BudgetResponse;
import com.finance.tracker.model.Budget;
import com.finance.tracker.model.User;
import com.finance.tracker.repository.BudgetRepository;
import com.finance.tracker.repository.TransactionRepository;
import com.finance.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    @Autowired private BudgetRepository budgetRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private UserRepository userRepository;

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    // ── CREATE or UPDATE budget for a category/month/year ─────────────────────
    @Transactional
    public BudgetResponse setbudget(String email, BudgetRequest req) {
        User user = getUser(email);

        // If budget already exists for this category/month/year, update it
        Budget budget = budgetRepository
                .findByUserIdAndCategoryAndMonthAndYear(
                    user.getId(), req.getCategory(), req.getMonth(), req.getYear())
                .orElse(new Budget());

        budget.setUser(user);
        budget.setCategory(req.getCategory());
        budget.setLimitAmount(req.getLimitAmount());
        budget.setMonth(req.getMonth());
        budget.setYear(req.getYear());

        Budget saved = budgetRepository.saveAndFlush(budget);
        Double spent = getSpent(user.getId(), saved);
        return BudgetResponse.from(saved, spent);
    }

    // ── GET all budgets for a month/year with live spending ───────────────────
    public List<BudgetResponse> getBudgets(String email, Integer month, Integer year) {
        User user = getUser(email);
        return budgetRepository.findByUserIdAndMonthAndYear(user.getId(), month, year)
                .stream()
                .map(b -> BudgetResponse.from(b, getSpent(user.getId(), b)))
                .collect(Collectors.toList());
    }

    // ── DELETE a budget ────────────────────────────────────────────────────────
    @Transactional
    public void deleteBudget(String email, Long id) {
        Budget b = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found."));
        if (!b.getUser().getEmail().equals(email))
            throw new RuntimeException("Access denied.");
        budgetRepository.delete(b);
    }

    // ── Helper: get actual spent amount from transactions ─────────────────────
    private Double getSpent(Long userId, Budget b) {
        Double spent = transactionRepository.sumByUserIdAndCategoryAndMonthAndYear(
                userId, b.getCategory(), b.getMonth(), b.getYear());
        return spent != null ? spent : 0.0;
    }
}
