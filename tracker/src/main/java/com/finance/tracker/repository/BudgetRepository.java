package com.finance.tracker.repository;

import com.finance.tracker.model.Budget;
import com.finance.tracker.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    // All budgets for a user in a given month/year
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND b.month = :month AND b.year = :year")
    List<Budget> findByUserIdAndMonthAndYear(
        @Param("userId") Long userId,
        @Param("month") Integer month,
        @Param("year") Integer year
    );

    // Find specific budget for a category/month/year
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND b.category = :category AND b.month = :month AND b.year = :year")
    Optional<Budget> findByUserIdAndCategoryAndMonthAndYear(
        @Param("userId") Long userId,
        @Param("category") Category category,
        @Param("month") Integer month,
        @Param("year") Integer year
    );
}
