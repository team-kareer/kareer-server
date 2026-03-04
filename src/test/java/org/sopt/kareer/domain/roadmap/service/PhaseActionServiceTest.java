package org.sopt.kareer.domain.roadmap.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.fixture.MemberFixture;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.roadmap.dto.response.AiGuideResponse;
import org.sopt.kareer.domain.roadmap.entity.Phase;
import org.sopt.kareer.domain.roadmap.entity.PhaseAction;
import org.sopt.kareer.domain.roadmap.entity.PhaseActionGuideline;
import org.sopt.kareer.domain.roadmap.entity.PhaseActionMistake;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseActionType;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseStatus;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;
import org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode;
import org.sopt.kareer.domain.roadmap.fixture.PhaseActionFixture;
import org.sopt.kareer.domain.roadmap.fixture.PhaseFixture;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionGuidelineRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionMistakeRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
public class PhaseActionServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PhaseRepository phaseRepository;

    @Autowired
    private PhaseActionRepository phaseActionRepository;

    @Autowired
    private PhaseActionMistakeRepository phaseActionMistakeRepository;

    @Autowired
    private PhaseActionGuidelineRepository phaseActionGuidelineRepository;

    @Autowired
    private PhaseActionService phaseActionService;

    @AfterEach
    void tearDown() {
        phaseActionGuidelineRepository.deleteAll();
        phaseActionMistakeRepository.deleteAll();
        phaseActionRepository.deleteAllInBatch();
        phaseRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("AI 가이드를 조회한다.")
    class GetAiGuide {

        @Test
        @DisplayName("AI 가이드를 성공적으로 조회한다.")
        void getAiGuide_success() {
            // given
            Member member1 = memberRepository.save(MemberFixture.getMember("test-provider-id-1"));
            Phase phase1 = phaseRepository.save(PhaseFixture.getPhase(member1, 1, PhaseStatus.CURRENT));
            PhaseAction phaseAction1 = phaseActionRepository.save(PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA));

            PhaseActionMistake mistake1 = PhaseActionMistake.create("test-content-1", phaseAction1);
            PhaseActionMistake mistake2 = PhaseActionMistake.create("test-content-2", phaseAction1);
            phaseActionMistakeRepository.saveAll(List.of(mistake1, mistake2));

            PhaseActionGuideline guideline1 = PhaseActionGuideline.create("test-content-1", phaseAction1);
            PhaseActionGuideline guideline2 = PhaseActionGuideline.create("test-content-2", phaseAction1);
            phaseActionGuidelineRepository.saveAll(List.of(guideline1, guideline2));

            // when
            AiGuideResponse response =
                    phaseActionService.getAiGuide(member1.getId(), phaseAction1.getId());

            // then
            assertThat(response).isNotNull();
            assertThat(response.importance()).isEqualTo("test-importance");
            assertThat(response.mistakes()).containsExactlyInAnyOrder("test-content-1", "test-content-2");
            assertThat(response.guidelines()).containsExactlyInAnyOrder("test-content-1", "test-content-2");
        }

        @Test
        @DisplayName("존재하지 않는 PhaseAction에 대한 AI 가이드를 조회할 경우 예외가 발생한다.")
        void getAiGuide_phaseActionNotFound() {
            // given
            Member member1 = memberRepository.save(MemberFixture.getMember("test-provider-id-1"));
            Long phaseActionId = 0L;

            // when & then
            assertThatThrownBy(() -> phaseActionService.getAiGuide(member1.getId(), phaseActionId))
                    .isInstanceOf(RoadMapException.class)
                    .extracting("errorCode")
                    .isEqualTo(RoadmapErrorCode.PHASE_ACTION_NOT_FOUND);
        }

        @Test
        @DisplayName("다른 회원의 PhaseAction에 대한 AI 가이드를 조회할 경우 예외가 발생한다.")
        void getRoadmapPhaseDetail_phaseActionNotFound_notOwner() {
            // given
            Member member1 = memberRepository.save(MemberFixture.getMember("test-provider-id-1"));
            Member member2 = memberRepository.save(MemberFixture.getMember("test-provider-id-2"));
            Phase phase1 = phaseRepository.save(PhaseFixture.getPhase(member1, 1, PhaseStatus.CURRENT));

            // when & then
            assertThatThrownBy(() -> phaseActionService.getAiGuide(member2.getId(), phase1.getId()))
                    .isInstanceOf(RoadMapException.class)
                    .extracting("errorCode")
                    .isEqualTo(RoadmapErrorCode.PHASE_ACTION_NOT_FOUND);
        }

        @Test
        @DisplayName("mistake와 guideline이 없어도 빈 리스트로 반환된다.")
        void getAiGuide_emptyLists() {
            // given
            Member member1 = memberRepository.save(MemberFixture.getMember("test-provider-id-1"));
            Phase phase1 = phaseRepository.save(PhaseFixture.getPhase(member1, 1, PhaseStatus.CURRENT));
            PhaseAction phaseAction = phaseActionRepository.save(PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA));

            // when
            AiGuideResponse response =
                    phaseActionService.getAiGuide(member1.getId(), phaseAction.getId());

            // then
            assertThat(response.mistakes()).isEmpty();
            assertThat(response.guidelines()).isEmpty();
        }
    }
}
