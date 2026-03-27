package org.sopt.kareer.domain.roadmap.dto.translation;

import java.util.List;

public record RoadmapTranslationTarget(List<PhaseTarget> phases) {

    public record PhaseTarget(
            Long phaseId,
            String goal,
            String description,
            List<PhaseActionTarget> actions
    ) {}

    public record PhaseActionTarget(
            Long phaseActionId,
            String title,
            String description,
            String importance,
            List<GuidelineTarget> guidelines,
            List<MistakeTarget> mistakes,
            List<ActionItemTarget> actionItems
    ) {}

    public record GuidelineTarget(Long guidelineId, String content) {}

    public record MistakeTarget(Long mistakeId, String content) {}

    public record ActionItemTarget(Long actionItemId, String title) {}
}
