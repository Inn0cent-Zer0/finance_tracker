package com.finance.tracker.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "budgets", uniqueConstraints = {
    // One budget per category per month per user
    @UniqueConstraint(columnNames = {"user_id", "category", "month", "year"})
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private Double limitAmount;   // budget limit set by user

    @Column(nullable = false)
    private Integer month;        // 1–12

    @Column(nullable = false)
    private Integer year;         // e.g. 2025
}
