package org.sopt.kareer.domain.roadmap.fixture;

import org.sopt.kareer.domain.roadmap.entity.Phase;
import org.sopt.kareer.domain.roadmap.entity.Roadmap;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseStatus;

import java.time.LocalDate;

public class PhaseFixture {

    public static final String GOAL = "test-goal";
    public static final String DESCRIPTION = "test-description";
    public static final LocalDate START_DATE = LocalDate.of(2026, 6, 1);
    public static final LocalDate END_DATE = LocalDate.of(2026, 8, 31);

    public static Phase getPhase(Roadmap roadmap, int sequence, PhaseStatus phaseStatus) {
        return Phase.create(roadmap, sequence, GOAL, DESCRIPTION, phaseStatus, START_DATE, END_DATE);
    }
}
