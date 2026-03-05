package com.finance.tracker.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance.tracker.dto.SavingsGoalRequest;
import com.finance.tracker.dto.SavingsGoalResponse;
import com.finance.tracker.model.SavingsGoal;
import com.finance.tracker.model.User;
import com.finance.tracker.repository.SavingsGoalRepository;
import com.finance.tracker.repository.UserRepository;

@Service
public class SavingsGoalService {

    @Autowired private SavingsGoalRepository savingsGoalRepository;
    @Autowired private UserRepository userRepository;

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    // ── CREATE ────────────────────────────────────────────────────────────────
    @Transactional
    public SavingsGoalResponse create(String email, SavingsGoalRequest req) {
        User user = getUser(email);

        SavingsGoal goal = new SavingsGoal();
        goal.setUser(user);
        goal.setGoalName(req.getGoalName());
        goal.setTargetAmount(req.getTargetAmount());
        goal.setSavedAmount(req.getSavedAmount() != null ? req.getSavedAmount() : 0.0);
        goal.setDeadline(req.getDeadline());
        goal.setAchieved(goal.getSavedAmount() >= goal.getTargetAmount());

        return SavingsGoalResponse.from(savingsGoalRepository.saveAndFlush(goal));
    }

    // ── GET ALL ───────────────────────────────────────────────────────────────
    public List<SavingsGoalResponse> getAll(String email) {
        User user = getUser(email);
        return savingsGoalRepository.findByUserId(user.getId())
                .stream().map(SavingsGoalResponse::from).collect(Collectors.toList());
    }

    // ── UPDATE (also used to add money toward goal) ────────────────────────────
    @Transactional
    public SavingsGoalResponse update(String email, Long id, SavingsGoalRequest req) {
        User user = getUser(email);
        SavingsGoal goal = savingsGoalRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Goal not found."));

        goal.setGoalName(req.getGoalName());
        goal.setTargetAmount(req.getTargetAmount());
        goal.setSavedAmount(req.getSavedAmount() != null ? req.getSavedAmount() : goal.getSavedAmount());
        goal.setDeadline(req.getDeadline());
        // Auto-mark achieved if saved >= target
        goal.setAchieved(goal.getSavedAmount() >= goal.getTargetAmount());

        return SavingsGoalResponse.from(savingsGoalRepository.saveAndFlush(goal));
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    @Transactional
    public void delete(String email, Long id) {
        User user = getUser(email);
        SavingsGoal goal = savingsGoalRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Goal not found."));
        savingsGoalRepository.delete(goal);
    }
}
