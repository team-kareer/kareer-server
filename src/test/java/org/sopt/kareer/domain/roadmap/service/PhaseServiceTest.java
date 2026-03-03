package org.sopt.kareer.domain.roadmap.service;

import org.junit.jupiter.api.AfterEach;
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

    @AfterEach
    void tearDown() {
        phaseRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("Phase 리스트를 정상적으로 조회한다.")
    @Test
    void getPhases_success() {
        // given
        Member member = memberRepository.save(MemberFixture.getMember());
        Phase phase1 = PhaseFixture.getPhase(member, 1, PhaseStatus.CURRENT);
        Phase phase2 = PhaseFixture.getPhase(member, 2, PhaseStatus.NEXT);
        Phase phase3 = PhaseFixture.getPhase(member, 3, PhaseStatus.FUTURE);
        phaseRepository.saveAll(List.of(phase1, phase2, phase3));

        // when
        PhaseListResponse response = phaseService.getPhases(member.getId());

        // then
        assertThat(response.phases()).hasSize(3);
    }
}
