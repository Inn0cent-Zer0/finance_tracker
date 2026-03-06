package com.finance.tracker.dto;

import com.finance.tracker.model.Category;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BudgetRequest {
    private Category category;
    private Double limitAmount;
    private Integer month;
    private Integer year;
}
