package org.sopt.kareer.domain.roadmap.fixture;

import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.roadmap.entity.Phase;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseStatus;

import java.time.LocalDate;

public class PhaseFixture {

    public static final String GOAL = "test-goal";
    public static final String DESCRIPTION = "test-description";

    public static Phase getPhase(Member member, int sequence, PhaseStatus phaseStatus, LocalDate startDate, LocalDate endDate) {
        return Phase.builder()
                .member(member)
                .sequence(sequence)
                .goal(GOAL)
                .description(DESCRIPTION)
                .status(phaseStatus)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
