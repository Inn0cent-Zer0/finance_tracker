package com.finance.tracker.dto;

import com.finance.tracker.model.SavingsGoal;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
public class SavingsGoalResponse {
    private Long id;
    private String goalName;
    private Double targetAmount;
    private Double savedAmount;
    private Double remaining;
    private Double percentAchieved;  // 0–100
    private LocalDate deadline;
    private Boolean achieved;

    public static SavingsGoalResponse from(SavingsGoal g) {
        SavingsGoalResponse r = new SavingsGoalResponse();
        r.id              = g.getId();
        r.goalName        = g.getGoalName();
        r.targetAmount    = g.getTargetAmount();
        r.savedAmount     = g.getSavedAmount();
        r.remaining       = Math.max(0, g.getTargetAmount() - g.getSavedAmount());
        r.percentAchieved = g.getTargetAmount() > 0
                            ? Math.min(100.0, (g.getSavedAmount() / g.getTargetAmount()) * 100)
                            : 0.0;
        r.deadline        = g.getDeadline();
        r.achieved        = g.getAchieved();
        return r;
    }
}
