package org.sopt.kareer.domain.roadmap.fixture;

import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.roadmap.entity.Phase;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseStatus;

import java.time.LocalDate;

public class PhaseFixture {

    public static final String GOAL = "test-goal";
    public static final String DESCRIPTION = "test-description";
    public static final LocalDate START_DATE = LocalDate.of(2026, 6, 1);
    public static final LocalDate END_DATE = LocalDate.of(2026, 8, 31);

    public static Phase getPhase(Member member, int sequence, PhaseStatus phaseStatus) {
        return Phase.builder()
                .member(member)
                .sequence(sequence)
                .goal(GOAL)
                .description(DESCRIPTION)
                .status(phaseStatus)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .build();
    }
}
