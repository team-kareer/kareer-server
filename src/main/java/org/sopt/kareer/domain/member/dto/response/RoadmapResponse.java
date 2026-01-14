package org.sopt.kareer.domain.member.dto.response;

import java.util.List;

public record RoadmapResponse(
        List<PhasePlan> phases
) {
    public record PhasePlan(
            String status,
            int sequence,
            String goal,
            String description,
            String startDate,
            String endDate,
            List<PhaseActionPlan> actions
    ) {}

    public record PhaseActionPlan(
            String title,
            String description,
            String type,
            String deadline,
            String importance,
            List<String> guideline,
            List<String> commonMistakes,
            List<ActionItemPlan> actionItems
    ) {}

    public record ActionItemPlan(
            String title,
            String actionsType,
            String deadline
    ) {}
}
