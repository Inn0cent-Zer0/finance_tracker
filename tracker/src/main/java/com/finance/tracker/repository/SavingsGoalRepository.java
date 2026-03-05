package com.finance.tracker.repository;

import com.finance.tracker.model.SavingsGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {

    @Query("SELECT g FROM SavingsGoal g WHERE g.user.id = :userId ORDER BY g.achieved ASC, g.deadline ASC")
    List<SavingsGoal> findByUserId(@Param("userId") Long userId);

    @Query("SELECT g FROM SavingsGoal g WHERE g.id = :id AND g.user.id = :userId")
    Optional<SavingsGoal> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
