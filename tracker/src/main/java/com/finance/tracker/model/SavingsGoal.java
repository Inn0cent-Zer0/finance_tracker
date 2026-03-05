package com.finance.tracker.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "savings_goals")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class SavingsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String goalName;          // e.g. "Buy a bike"

    @Column(nullable = false)
    private Double targetAmount;      // how much to save

    @Column(nullable = false)
    private Double savedAmount;       // how much saved so far

    private LocalDate deadline;       // optional target date

    @Column(nullable = false)
    private Boolean achieved = false; // auto-set when savedAmount >= targetAmount
}
