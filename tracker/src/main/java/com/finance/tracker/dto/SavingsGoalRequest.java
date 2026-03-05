package com.finance.tracker.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class SavingsGoalRequest {
    private String goalName;          
    private Double targetAmount;     
    private Double savedAmount; // can update this for track progress.
    private LocalDate deadline;      
}
