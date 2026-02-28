package com.finance.tracker.dto;
import com.finance.tracker.model.Category;
import com.finance.tracker.model.TransactionType;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
@Getter @Setter

public class TransactionRequest {
    private TransactionType type;  
    private Category category;
    private Double amount;
    private String description;
    private LocalDate date;
}
