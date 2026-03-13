package org.sopt.kareer.domain.roadmap.service;

import org.junit.jupiter.api.*;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.fixture.MemberFixture;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.roadmap.dto.response.HomePhaseDetailResponse;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseListResponse;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseResponse;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapPhaseDetailResponse;
import org.sopt.kareer.domain.roadmap.entity.Phase;
import org.sopt.kareer.domain.roadmap.entity.PhaseAction;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseActionType;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseStatus;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;
import org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode;
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

import static org.assertj.core.api.Assertions.*;

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

    private Phase phase1;
    private Member member1;

    @BeforeEach
    void setUp() {
        member1 = memberRepository.save(MemberFixture.getMember("test-provider-id-1"));
        phase1 = phaseRepository.save(PhaseFixture.getPhase(member1, 1, PhaseStatus.CURRENT));
    }

    @AfterEach
    void tearDown() {
        phaseActionRepository.deleteAllInBatch();
        phaseRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("Phase 리스트를 조회한다.")
    class GetPhases {

        @Test
        @DisplayName("Phase 리스트를 조회한다.")
        void getPhases_success() {
            // given
            Phase phase2 = PhaseFixture.getPhase(member1, 2, PhaseStatus.NEXT);
            Phase phase3 = PhaseFixture.getPhase(member1, 3, PhaseStatus.FUTURE);
            phaseRepository.saveAll(List.of(phase1, phase2, phase3));

            // when
            PhaseListResponse response = phaseService.getPhases(member1.getId());

            // then
            assertThat(response.phases()).hasSize(3);
            assertThat(response.phases())
                    .extracting(PhaseResponse::phaseId)
                    .containsExactly(
                            phase1.getId(),
                            phase2.getId(),
                            phase3.getId()
                    );
        }

        @Test
        @DisplayName("다른 회원의 Phase 리스트는 조회되지 않는다.")
        void getPhases_onlyOwnData() {
            // given
            Member member2 = memberRepository.save(MemberFixture.getMember("test-provider-id-2"));
            Phase phase2 = PhaseFixture.getPhase(member1, 2, PhaseStatus.NEXT);
            Phase phase3 = PhaseFixture.getPhase(member1, 3, PhaseStatus.FUTURE);
            Phase phase4 = PhaseFixture.getPhase(member2, 2, PhaseStatus.CURRENT);
            phaseRepository.saveAll(List.of(phase1, phase2, phase3, phase4));

            // when
            PhaseListResponse response = phaseService.getPhases(member1.getId());

            // then
            assertThat(response.phases()).hasSize(3);
            assertThat(response.phases())
                    .extracting(PhaseResponse::phaseId)
                    .containsExactlyInAnyOrder(
                            phase1.getId(),
                            phase2.getId(),
                            phase3.getId()
                    );
        }
    }

    @Nested
    @DisplayName("로드맵 Phase 상세정보를 조회한다.")
    class GetRoadmapPhaseDetail {

        @Test
        @DisplayName("로드맵 phase 상세정보 조회 시 액션들이 타입별로 그룹화되어 지정된 순서대로 반환된다.")
        void getRoadmapPhaseDetail_grouped() {
            // given
            PhaseAction phaseActionVisa = PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA);

            PhaseAction phaseActionDone = PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA);
            phaseActionDone.markAdded();
            phaseActionDone.markCompleted();

            PhaseAction phaseActionCareer = PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.CAREER);

            phaseActionRepository.saveAll(List.of(phaseActionVisa, phaseActionDone, phaseActionCareer));

            // when
            RoadmapPhaseDetailResponse response = phaseService.getRoadmapPhaseDetail(member1.getId(), phase1.getId());

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
            PhaseAction phaseActionVisa1 = PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA);
            PhaseAction phaseActionVisa2 = PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA);
            phaseActionRepository.saveAll(List.of(phaseActionVisa1, phaseActionVisa2));

            // when
            RoadmapPhaseDetailResponse response = phaseService.getRoadmapPhaseDetail(member1.getId(), phase1.getId());

            // then
            assertThat(response.actions().get("Career").items().isEmpty()).isTrue();
            assertThat(response.actions().get("Career").count()).isEqualTo(0);
        }

        @Test
        @DisplayName("로드맵 Phase 상세정보를 조회시 그룹 내 아이템은 deadline 오름차순, 동일 시 title 오름차순으로 정렬된다.")
        void getRoadmapPhaseDetail_ordered() {
            // given
            PhaseAction phaseActionVisaLateTitle1 = PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA, "test-title-1", LocalDate.of(2026, 3,2));
            PhaseAction phaseActionVisaLateTitle2 = PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA,"test-title-2", LocalDate.of(2026, 3,2));
            PhaseAction phaseActionVisaEarlyTitle1 = PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA, "test-title-1", LocalDate.of(2026, 3,1));

            phaseActionRepository.saveAll(List.of(phaseActionVisaLateTitle1, phaseActionVisaLateTitle2, phaseActionVisaEarlyTitle1));

            // when
            RoadmapPhaseDetailResponse response = phaseService.getRoadmapPhaseDetail(member1.getId(), phase1.getId());

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
        @DisplayName("특정 로드맵 Phase 상세정보 조회 시, 다른 Phase의 Action은 포함되지 않는다.")
        void getRoadmapPhaseDetail_onlyParticularPhaseData() {
            // given
            Phase phase2 = phaseRepository.save(PhaseFixture.getPhase(member1, 1, PhaseStatus.CURRENT));
            PhaseAction phaseActionVisa1 = PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA);
            PhaseAction phaseActionVisa2 = PhaseActionFixture.getPhaseAction(phase2, PhaseActionType.VISA);
            phaseActionRepository.saveAll(List.of(phaseActionVisa1, phaseActionVisa2));

            // when
            RoadmapPhaseDetailResponse response = phaseService.getRoadmapPhaseDetail(member1.getId(), phase1.getId());

            // then
            assertThat(response.actions().get("Visa").items()).hasSize(1);
            assertThat(response.actions().get("Visa").items())
                    .extracting(RoadmapPhaseDetailResponse.ActionGroupResponse.ActionResponse::phaseActionId)
                    .containsExactly(phaseActionVisa1.getId());
        }

        @Test
        @DisplayName("존재하지 않는 로드맵 Phase 상세정보를 조회할 경우 예외가 발생한다.")
        void getRoadmapPhaseDetail_phaseNotFound() {
            // given
            Long phaseId = 0L;

            // when & then
            assertThatThrownBy(() -> phaseService.getRoadmapPhaseDetail(member1.getId(), phaseId))
                    .isInstanceOf(RoadMapException.class)
                    .extracting("errorCode")
                    .isEqualTo(RoadmapErrorCode.PHASE_NOT_FOUND);
        }

        @Test
        @DisplayName("다른 회원의 로드맵 Phase 상세정보를 조회할 경우 예외가 발생한다.")
        void getRoadmapPhaseDetail_phaseNotFound_notOwner() {
            // given
            Member member2 = memberRepository.save(MemberFixture.getMember("test-provider-id-2"));

            // when & then
            assertThatThrownBy(() -> phaseService.getRoadmapPhaseDetail(member2.getId(), phase1.getId()))
                    .isInstanceOf(RoadMapException.class)
                    .extracting("errorCode")
                    .isEqualTo(RoadmapErrorCode.PHASE_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("홈 Phase 상세정보를 조회한다.")
    class GetHomePhaseDetail {

        @Test
        @DisplayName("홈 phase 상세정보 조회 시 미완료 Actions들만 반환된다.")
        void getHomePhaseDetail_completed() {
            // given
            PhaseAction phaseActionIncomplete = PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA);

            PhaseAction phaseActionComplete = PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA);
            phaseActionComplete.markAdded();
            phaseActionComplete.markCompleted();

            phaseActionRepository.saveAll(List.of(phaseActionIncomplete, phaseActionComplete));

            // when
            HomePhaseDetailResponse response = phaseService.getHomePhaseDetail(member1.getId(), phase1.getId());

            // then
            assertThat(response.count()).isEqualTo(1);
            assertThat(response.actions()).hasSize(1);

            assertThat(response.actions().get(0).phaseActionId()).isEqualTo(phaseActionIncomplete.getId());
            assertThat(response.actions().get(0).type()).isEqualTo(phaseActionIncomplete.getType().getDisplayName());
            assertThat(response.actions().get(0).title()).isEqualTo(phaseActionIncomplete.getTitle());
            assertThat(response.actions().get(0).deadline()).isEqualTo(phaseActionIncomplete.getDeadline());
        }

        @Test
        @DisplayName("특정 홈 Phase 상세정보 조회 시, 다른 Phase의 Action은 포함되지 않는다.")
        void getHomePhaseDetail_onlyParticularPhaseData() {
            // given
            Phase phase2 = phaseRepository.save(PhaseFixture.getPhase(member1, 1, PhaseStatus.CURRENT));
            PhaseAction phaseActionVisa1 = PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA);
            PhaseAction phaseActionVisa2 = PhaseActionFixture.getPhaseAction(phase2, PhaseActionType.VISA);
            phaseActionRepository.saveAll(List.of(phaseActionVisa1, phaseActionVisa2));

            // when
            HomePhaseDetailResponse response = phaseService.getHomePhaseDetail(member1.getId(), phase1.getId());

            // then
            assertThat(response.actions()).hasSize(1);
            assertThat(response.actions())
                    .extracting(HomePhaseDetailResponse.HomePhaseActionResponse::phaseActionId)
                    .containsExactly(phaseActionVisa1.getId());
        }

        @Test
        @DisplayName("존재하지 않는 홈 Phase 상세정보를 조회할 경우 예외가 발생한다.")
        void getHomePhaseDetail_phaseNotFound() {
            // given
            Long phaseId = 0L;

            // when & then
            assertThatThrownBy(() -> phaseService.getHomePhaseDetail(member1.getId(), phaseId))
                    .isInstanceOf(RoadMapException.class)
                    .extracting("errorCode")
                    .isEqualTo(RoadmapErrorCode.PHASE_NOT_FOUND);
        }

        @Test
        @DisplayName("다른 회원의 홈 Phase 상세정보를 조회할 경우 예외가 발생한다.")
        void getHomePhaseDetail_phaseNotFound_notOwner() {
            // given
            Member member2 = memberRepository.save(MemberFixture.getMember("test-provider-id-2"));

            // when & then
            assertThatThrownBy(() -> phaseService.getHomePhaseDetail(member2.getId(), phase1.getId()))
                    .isInstanceOf(RoadMapException.class)
                    .extracting("errorCode")
                    .isEqualTo(RoadmapErrorCode.PHASE_NOT_FOUND);
        }
    }
}
