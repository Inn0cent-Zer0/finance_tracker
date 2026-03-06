package com.finance.tracker.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
public class SavingsGoalRequest {
    private String goalName;
    private Double targetAmount;
    private Double savedAmount;    // can update this to track progress
    private LocalDate deadline;
}
