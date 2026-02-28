package com.finance.tracker.dto;

import com.finance.tracker.model.Category;
import com.finance.tracker.model.Transaction;
import com.finance.tracker.model.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class TransactionResponse {
    private Long id;
    private TransactionType type;
    private Category category;
    private Double amount;
    private String description;
    private LocalDate date;

    // Build from entity
    public static TransactionResponse from(Transaction t) {
        TransactionResponse r = new TransactionResponse();
        r.id          = t.getId();
        r.type        = t.getType();
        r.category    = t.getCategory();
        r.amount      = t.getAmount();
        r.description = t.getDescription();
        r.date        = t.getDate();
        return r;
    }
}
