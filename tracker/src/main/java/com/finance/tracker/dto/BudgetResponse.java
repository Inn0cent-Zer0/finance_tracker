package com.finance.tracker.dto;

import com.finance.tracker.model.Budget;
import com.finance.tracker.model.Category;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BudgetResponse {
    private Long id;
    private Category category;
    private Double limitAmount;
    private Double spentAmount;    // calculated from transactions
    private Double remaining;      // limitAmount - spentAmount
    private Double percentUsed;    // 0–100
    private Integer month;
    private Integer year;

    public static BudgetResponse from(Budget b, Double spent) {
        BudgetResponse r = new BudgetResponse();
        r.id          = b.getId();
        r.category    = b.getCategory();
        r.limitAmount = b.getLimitAmount();
        r.spentAmount = spent != null ? spent : 0.0;
        r.remaining   = b.getLimitAmount() - r.spentAmount;
        r.percentUsed = b.getLimitAmount() > 0
                        ? Math.min(100.0, (r.spentAmount / b.getLimitAmount()) * 100)
                        : 0.0;
        r.month       = b.getMonth();
        r.year        = b.getYear();
        return r;
    }
}
