package com.finance.tracker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private Double income;
    private Double savings;
    private Double targetExpenses;
}