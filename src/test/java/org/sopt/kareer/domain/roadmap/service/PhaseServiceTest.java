package org.sopt.kareer.domain.roadmap.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.fixture.MemberFixture;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseListResponse;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseStatus;
import org.sopt.kareer.domain.roadmap.fixture.PhaseFixture;
import org.sopt.kareer.domain.roadmap.repository.PhaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class PhaseServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PhaseRepository phaseRepository;

    @Autowired
    private PhaseService phaseService;

    @DisplayName("Phase 리스트를 정상적으로 조회한다.")
    @Test
    void getPhases_success() {
        // given
        Member member = memberRepository.save(MemberFixture.getMember());

        phaseRepository.save(PhaseFixture.getPhase(
                member,
                1,
                PhaseStatus.CURRENT,
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 5, 31)
        ));

        phaseRepository.save(PhaseFixture.getPhase(
                member,
                2,
                PhaseStatus.NEXT,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 8, 31)
        ));

        phaseRepository.save(PhaseFixture.getPhase(
                member,
                3,
                PhaseStatus.FUTURE,
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 11, 30)
        ));

        // when
        PhaseListResponse response = phaseService.getPhases(member.getId());

        // then
        assertThat(response.phases()).hasSize(3);
    }
}
