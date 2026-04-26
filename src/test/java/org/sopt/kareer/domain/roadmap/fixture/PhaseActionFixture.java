package org.sopt.kareer.domain.roadmap.fixture;

import org.sopt.kareer.domain.roadmap.entity.enums.PhaseActionType;
import org.sopt.kareer.domain.roadmap.entity.phase.Phase;
import org.sopt.kareer.domain.roadmap.entity.phaseaction.PhaseAction;

import java.time.LocalDate;

public class PhaseActionFixture {

    public static final String TITLE = "test-title";
    public static final String DESCRIPTION = "test-description";
    public static final LocalDate DEADLINE = LocalDate.of(2026, 3, 4);
    public static final String IMPORTANCE = "test-importance";

    public static PhaseAction getPhaseAction(Phase phase, PhaseActionType type) {
        return PhaseAction.create(TITLE, DESCRIPTION, type, DEADLINE, IMPORTANCE, phase);
    }

    public static PhaseAction getPhaseAction(Phase phase, PhaseActionType type, LocalDate deadline) {
        return PhaseAction.create(TITLE, DESCRIPTION, type, deadline, IMPORTANCE, phase);
    }

    public static PhaseAction getPhaseAction(Phase phase, PhaseActionType type, String title, LocalDate deadline) {
        return PhaseAction.create(title, DESCRIPTION, type, deadline, IMPORTANCE, phase);
    }
}
