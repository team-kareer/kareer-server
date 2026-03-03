package org.sopt.kareer.domain.roadmap.fixture;

import org.sopt.kareer.domain.roadmap.entity.Phase;
import org.sopt.kareer.domain.roadmap.entity.PhaseAction;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseActionType;

import java.time.LocalDate;

public class PhaseActionFixture {

    public static final String TITLE = "test-title";
    public static final String DESCRIPTION = "test-description";
    public static final LocalDate DEADLINE = LocalDate.of(2026, 3, 4);
    public static final String IMPORTANCE = "test-importance";

    public static PhaseAction.PhaseActionBuilder builder(Phase phase, PhaseActionType type) {
        return PhaseAction.builder()
                .title(TITLE)
                .description(DESCRIPTION)
                .type(type)
                .deadline(DEADLINE)
                .importance(IMPORTANCE)
                .phase(phase);
    }

    public static PhaseAction getPhaseAction(Phase phase, PhaseActionType type) {
        return builder(phase, type).build();
    }

    public static PhaseAction getPhaseAction(Phase phase, PhaseActionType type, LocalDate deadline) {
        return builder(phase, type)
                .deadline(deadline)
                .build();
    }

    public static PhaseAction getPhaseAction(Phase phase, PhaseActionType type, String title, LocalDate deadline) {
        return builder(phase, type)
                .title(title)
                .deadline(deadline)
                .build();
    }
}
