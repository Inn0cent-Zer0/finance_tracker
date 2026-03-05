package com.finance.tracker.repository;

import com.finance.tracker.model.Category;
import com.finance.tracker.model.Transaction;
import com.finance.tracker.model.TransactionType;
import com.finance.tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // All transactions for a user, newest first
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId ORDER BY t.date DESC")
    List<Transaction> findByUserId(@Param("userId") Long userId);

    // Filter by type (INCOME / EXPENSE)
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.type = :type ORDER BY t.date DESC")
    List<Transaction> findByUserIdAndType(@Param("userId") Long userId, @Param("type") TransactionType type);

    // Filter by category
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.category = :category ORDER BY t.date DESC")
    List<Transaction> findByUserIdAndCategory(@Param("userId") Long userId, @Param("category") Category category);

    // Filter by date range
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.date BETWEEN :from AND :to ORDER BY t.date DESC")
    List<Transaction> findByUserIdAndDateBetween(@Param("userId") Long userId, @Param("from") LocalDate from, @Param("to") LocalDate to);

    // Total income or expense for a user
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type")
    Double sumByUserIdAndType(@Param("userId") Long userId, @Param("type") TransactionType type);

    // Total spent on a specific category in a specific month/year (for budget tracking)
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.category = :category " +
           "AND t.type = 'EXPENSE' " +
           "AND FUNCTION('MONTH', t.date) = :month AND FUNCTION('YEAR', t.date) = :year")
    Double sumByUserIdAndCategoryAndMonthAndYear(
        @Param("userId") Long userId,
        @Param("category") Category category,
        @Param("month") Integer month,
        @Param("year") Integer year
    );
}

