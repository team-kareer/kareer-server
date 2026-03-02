package org.sopt.kareer.domain.roadmap.fixture;

import org.sopt.kareer.domain.roadmap.dto.response.PhaseResponse;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseStatus;

import java.time.LocalDate;

public class PhaseResponseFixture {

    public static final Long PHASE_ID = 1L;
    public static final String GOAL = "test-goal";
    public static final String DESCRIPTION = "test-description";
    public static final Long WORKS_COUNT = 1L;
    public static final LocalDate START_DATE = LocalDate.of(2025, 3, 1);
    public static final LocalDate END_DATE = LocalDate.of(2025, 5, 31);

    public static PhaseResponse of() {
        return new PhaseResponse(
                PHASE_ID,
                PhaseStatus.CURRENT,
                1,
                GOAL,
                DESCRIPTION,
                WORKS_COUNT,
                START_DATE,
                END_DATE
        );
    }
}
