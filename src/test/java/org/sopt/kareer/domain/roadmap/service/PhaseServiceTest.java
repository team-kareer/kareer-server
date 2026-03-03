package org.sopt.kareer.domain.roadmap.service;

import org.junit.jupiter.api.*;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.fixture.MemberFixture;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseListResponse;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapPhaseDetailResponse;
import org.sopt.kareer.domain.roadmap.entity.Phase;
import org.sopt.kareer.domain.roadmap.entity.PhaseAction;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseActionType;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseStatus;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;
import org.sopt.kareer.domain.roadmap.fixture.PhaseActionFixture;
import org.sopt.kareer.domain.roadmap.fixture.PhaseFixture;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class PhaseServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PhaseRepository phaseRepository;

    @Autowired
    private PhaseService phaseService;

    @Autowired
    private PhaseActionRepository phaseActionRepository;

    @AfterEach
    void tearDown() {
        phaseActionRepository.deleteAllInBatch();
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

    @Nested
    @DisplayName("로드맵 Phase 상세정보를 조회한다.")
    class GetRoadmapPhaseDetail {

        private Phase phase;
        private Member member;

        @BeforeEach
        void setUp() {
            member = memberRepository.save(MemberFixture.getMember());
            phase = phaseRepository.save(PhaseFixture.getPhase(member, 1, PhaseStatus.CURRENT));
        }

        @Test
        @DisplayName("로드맵 phase 상세정보 조회 시 액션들이 타입별로 그룹화되어 지정된 순서대로 반환된다.")
        void getRoadmapPhaseDetail_grouped() {
            // given
            PhaseAction phaseActionVisa = PhaseActionFixture.getPhaseAction(phase, PhaseActionType.VISA);

            PhaseAction phaseActionDone = PhaseActionFixture.getPhaseAction(phase, PhaseActionType.VISA);
            phaseActionDone.markAdded();
            phaseActionDone.markCompleted();

            PhaseAction phaseActionCareer = PhaseActionFixture.getPhaseAction(phase, PhaseActionType.CAREER);

            phaseActionRepository.saveAll(List.of(phaseActionVisa, phaseActionDone, phaseActionCareer));

            // when
            RoadmapPhaseDetailResponse response = phaseService.getRoadmapPhaseDetail(member.getId(), phase.getId());

            // then
            assertThat(response.totalCount()).isEqualTo(3);

            List<String> keys = new ArrayList<>(response.actions().keySet());
            assertThat(keys).containsExactly("Visa", "Career", "Done");

            assertThat(response.actions().get("Visa").count()).isEqualTo(1);
            assertThat(response.actions().get("Career").count()).isEqualTo(1);
            assertThat(response.actions().get("Done").count()).isEqualTo(1);
        }

        @Test
        @DisplayName("로드맵 Phase 상세정보 조회시 그룹에 item이 없으면 빈 리스트를 반환한다.")
        void getRoadmapPhaseDetail_emptyGrouped() {
            // given
            PhaseAction phaseActionVisa1 = PhaseActionFixture.getPhaseAction(phase, PhaseActionType.VISA);
            PhaseAction phaseActionVisa2 = PhaseActionFixture.getPhaseAction(phase, PhaseActionType.VISA);
            phaseActionRepository.saveAll(List.of(phaseActionVisa1, phaseActionVisa2));

            // when
            RoadmapPhaseDetailResponse response = phaseService.getRoadmapPhaseDetail(member.getId(), phase.getId());

            // then
            assertThat(response.actions().get("Career").items().isEmpty()).isTrue();
            assertThat(response.actions().get("Career").count()).isEqualTo(0);
        }

        @Test
        @DisplayName("로드맵 Phase 상세정보를 조회시 그룹 내 아이템은 deadline 오름차순, 동일 시 title 오름차순으로 정렬된다.")
        void getRoadmapPhaseDetail_ordered() {
            // given
            PhaseAction phaseActionVisaLateTitle1 = PhaseActionFixture.getPhaseAction(phase, PhaseActionType.VISA, "test-title-1", LocalDate.of(2026, 3,2));
            PhaseAction phaseActionVisaLateTitle2 = PhaseActionFixture.getPhaseAction(phase, PhaseActionType.VISA,"test-title-2", LocalDate.of(2026, 3,2));
            PhaseAction phaseActionVisaEarlyTitle1 = PhaseActionFixture.getPhaseAction(phase, PhaseActionType.VISA, "test-title-1", LocalDate.of(2026, 3,1));

            phaseActionRepository.saveAll(List.of(phaseActionVisaLateTitle1, phaseActionVisaLateTitle2, phaseActionVisaEarlyTitle1));

            // when
            RoadmapPhaseDetailResponse response = phaseService.getRoadmapPhaseDetail(member.getId(), phase.getId());

            // then
            assertThat(response.actions().get("Visa").items())
                    .extracting("deadline", "title")
                    .containsExactly(
                            tuple(LocalDate.of(2026, 3, 1), "test-title-1"),
                            tuple(LocalDate.of(2026, 3, 2), "test-title-1"),
                            tuple(LocalDate.of(2026, 3, 2), "test-title-2")
                    );
        }

        @Test
        @DisplayName("존재하지 않는 로드맵 Phase 상세정보를 조회할 경우 예외가 발생한다.")
        void getRoadmapPhaseDetail_phaseNotFound() {
            // given
            Long phaseId = 0L;

            // when & then
            assertThrows(RoadMapException.class,
                    () -> phaseService.getRoadmapPhaseDetail(member.getId(), phaseId));
        }
    }
}
