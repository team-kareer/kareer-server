package org.sopt.kareer.domain.roadmap.repository;

import org.junit.jupiter.api.*;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.fixture.MemberFixture;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseResponse;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapPhaseDetailResponse;
import org.sopt.kareer.domain.roadmap.entity.Phase;
import org.sopt.kareer.domain.roadmap.entity.PhaseAction;
import org.sopt.kareer.domain.roadmap.entity.Roadmap;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseActionType;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseStatus;
import org.sopt.kareer.domain.roadmap.fixture.PhaseActionFixture;
import org.sopt.kareer.domain.roadmap.fixture.PhaseFixture;
import org.sopt.kareer.global.config.QuerydslConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@Import(QuerydslConfig.class)
public class PhaseRepositoryTest {

    @Autowired
    private PhaseRepository phaseRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RoadmapRepository roadmapRepository;

    @Autowired
    private PhaseActionRepository phaseActionRepository;

    private Phase phase1;
    private Member member1;
    private Roadmap roadmap1;

    @BeforeEach
    void setUp() {
        member1 = memberRepository.save(MemberFixture.getMember("test-provider-id-1"));
        roadmap1 = roadmapRepository.save(Roadmap.create(member1));
        phase1 = phaseRepository.save(PhaseFixture.getPhase(roadmap1, 1, PhaseStatus.CURRENT));
    }

    @Nested
    @DisplayName("phase 리스트를 반환한다.")
    class FindPhases {

        @Test
        @DisplayName("phase 리스트가 sequence 오름차순 정렬되어 반환된다.")
        void findPhases_ordered() {
            // given
            Phase phase3 = PhaseFixture.getPhase(roadmap1, 3, PhaseStatus.FUTURE);
            Phase phase2 = PhaseFixture.getPhase(roadmap1, 2, PhaseStatus.NEXT);
            phaseRepository.saveAll(List.of(phase2, phase3));

            // when
            List<PhaseResponse> response = phaseRepository.findPhases(member1.getId());

            // then
            assertThat(response).hasSize(3);

            assertThat(response.get(0).sequence()).isEqualTo(1);
            assertThat(response.get(1).sequence()).isEqualTo(2);
            assertThat(response.get(2).sequence()).isEqualTo(3);
        }

        @Test
        @DisplayName("다른 회원의 Phase 리스트는 반환되지 않는다.")
        void findPhases_onlyOwnData() {
            // given
            Member member2 = memberRepository.save(MemberFixture.getMember("test-provider-id-2"));
            Roadmap roadmap2 = roadmapRepository.save(Roadmap.create(member2));
            Phase phase2 = phaseRepository.save(PhaseFixture.getPhase(roadmap2, 1, PhaseStatus.CURRENT));

            // when
            List<PhaseResponse> response = phaseRepository.findPhases(member1.getId());

            // then
            assertThat(response).hasSize(1);
            assertThat(response)
                    .extracting(PhaseResponse::phaseId)
                    .contains(phase1.getId())
                    .doesNotContain(phase2.getId());
        }
    }

    @Nested
    @DisplayName("로드맵 phase 상세정보를 반환한다.")
    class GetRoadmapPhaseDetail {

        @Test
        @DisplayName("PhaseAction들을 타입별로 그룹핑하여 Map으로 반환한다.")
        void getRoadmapPhaseDetail_grouped() {
            // given
            PhaseAction phaseActionVisa = PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA);
            phaseActionVisa.markAdded();

            PhaseAction phaseActionCareer  = PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.CAREER);

            PhaseAction phaseActionDone = PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA);
            phaseActionDone.markAdded();
            phaseActionDone.markCompleted();

            phaseActionRepository.saveAll(List.of(phaseActionVisa, phaseActionCareer, phaseActionDone));

            // when
            Map<String, List<RoadmapPhaseDetailResponse.ActionGroupResponse.ActionResponse>> response =
                    phaseRepository.getRoadmapPhaseDetail(phase1.getId());

            // then
            assertThat(response).containsKeys("Visa", "Career", "Done");

            assertThat(response.get("Visa")).hasSize(1);
            assertThat(response.get("Career")).hasSize(1);
            assertThat(response.get("Done")).hasSize(1);
        }

        @Test
        @DisplayName("다른 회원의 Phase 상세정보는 반환되지 않는다.")
        void getRoadmapPhaseDetail_onlyOwnData() {
            // given
            Member member2 = memberRepository.save(MemberFixture.getMember("test-provider-id-2"));
            Roadmap roadmap2 = roadmapRepository.save(Roadmap.create(member2));
            Phase phase2 = phaseRepository.save(PhaseFixture.getPhase(roadmap2, 1, PhaseStatus.CURRENT));

            PhaseAction phaseActionVisa1 = PhaseActionFixture.getPhaseAction(phase1, PhaseActionType.VISA);
            PhaseAction phaseActionVisaOfOtherMember = PhaseActionFixture.getPhaseAction(phase2, PhaseActionType.CAREER);
            phaseActionRepository.saveAll(List.of(phaseActionVisa1, phaseActionVisaOfOtherMember));

            // when
            Map<String, List<RoadmapPhaseDetailResponse.ActionGroupResponse.ActionResponse>> response = phaseRepository.getRoadmapPhaseDetail(phase1.getId());

            // then
            assertThat(response.get("Visa")).hasSize(1);
            assertThat(response).containsOnlyKeys("Visa");
        }
    }

    @Nested
    @DisplayName("phaseId와 memberId가 일치하는 Phase의 존재 여부를 반환한다.")
    class ExistsByIdAndMemberId {

        @Test
        @DisplayName("phaseId와 memberId가 일치하는 Phase가 존재하면 true를 반환한다.")
        void existsByIdAndMemberId_true() {
            // when
            boolean result = phaseRepository.existsByIdAndRoadmap_Member_Id(phase1.getId(), member1.getId());

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("phaseId와 memberId가 일치하는 Phase가 존재하지 않으면 false를 반환한다.")
        void existsByIdAndMemberId_false() {
            // given
            Member member2 = memberRepository.save(MemberFixture.getMember("test-provider-id-2"));

            // when
            boolean result = phaseRepository.existsByIdAndRoadmap_Member_Id(phase1.getId(), member2.getId());

            // then
            assertThat(result).isFalse();
        }
    }
}
