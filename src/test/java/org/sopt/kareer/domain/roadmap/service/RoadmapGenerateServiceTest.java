package org.sopt.kareer.domain.roadmap.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.roadmap.entity.Roadmap;
import org.sopt.kareer.domain.roadmap.entity.enums.RoadmapActiveStatus;
import org.sopt.kareer.domain.roadmap.progress.RoadmapGenerationStep;
import org.sopt.kareer.domain.roadmap.progress.RoadmapProgressNotifier;
import org.sopt.kareer.domain.roadmap.repository.ActionItemRepository;
import org.sopt.kareer.domain.roadmap.repository.RoadmapRepository;
import org.sopt.kareer.domain.roadmap.service.dto.response.RoadmapGenerationContext;
import org.sopt.kareer.global.external.ai.builder.context.MemberContextBuilder;
import org.sopt.kareer.global.external.ai.service.PolicyDocumentRetriever;
import org.sopt.kareer.global.external.ai.service.RequiredDocumentRetriever;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RoadmapGenerateServiceTest {

    @Mock
    private MemberContextBuilder memberContextBuilder;
    @Mock
    private RequiredDocumentRetriever requiredRetriever;
    @Mock
    private PolicyDocumentRetriever policyDocumentRetriever;
    @Mock
    private RoadmapRepository roadmapRepository;
    @Mock
    private ActionItemRepository actionItemRepository;
    @Mock
    private RoadmapProgressNotifier progress;
    @Mock
    private Member member;
    @Mock
    private MemberVisa visa;

    private RoadmapGenerateService roadmapGenerateService;

    @BeforeEach
    void setUp() {
        roadmapGenerateService = new RoadmapGenerateService(
                memberContextBuilder,
                requiredRetriever,
                policyDocumentRetriever,
                roadmapRepository,
                actionItemRepository
        );
    }

    @Test
    void 사용자_분석_완료_후_정책_검색을_시작한다() {
        given(member.getId()).willReturn(1L);
        given(roadmapRepository.findByMember_IdAndStatus(1L, org.sopt.kareer.domain.roadmap.entity.enums.RoadmapActiveStatus.ACTIVE))
                .willReturn(Optional.empty());
        given(memberContextBuilder.load(1L))
                .willReturn(new MemberContextBuilder.MemberAndContext(member, "member-context"));
        given(requiredRetriever.retrieveVisaAll(visa)).willReturn(List.of());
        given(requiredRetriever.retrieveCareer(member))
                .willReturn(new RequiredDocumentRetriever.CareerSelectedDocs(
                        List.of(),
                        List.of(),
                        List.of()
                ));
        given(policyDocumentRetriever.retrievePolicy(member, visa)).willReturn(List.of());

        RoadmapGenerationContext result = roadmapGenerateService.prepareGeneration(
                member,
                visa,
                progress
        );

        InOrder order = inOrder(progress);
        order.verify(progress).completed(RoadmapGenerationStep.USER_ANALYSIS);
        order.verify(progress).started(RoadmapGenerationStep.POLICY_SEARCH);
        assertThat(result.memberContextText()).isEqualTo("member-context");
    }

    @Test
    void 사용자_Context_생성에_실패하면_사용자_분석_실패를_알린다() {
        given(member.getId()).willReturn(1L);
        given(roadmapRepository.findByMember_IdAndStatus(1L, org.sopt.kareer.domain.roadmap.entity.enums.RoadmapActiveStatus.ACTIVE))
                .willReturn(Optional.empty());
        given(memberContextBuilder.load(1L))
                .willThrow(new IllegalStateException("context failure"));

        assertThatThrownBy(() -> roadmapGenerateService.prepareGeneration(
                member,
                visa,
                progress
        )).isInstanceOf(IllegalStateException.class);

        verify(progress).failed(RoadmapGenerationStep.USER_ANALYSIS);
    }

    @Test
    void 문서_검색에_실패하면_정책_검색_실패를_알린다() {
        given(member.getId()).willReturn(1L);
        given(roadmapRepository.findByMember_IdAndStatus(1L, org.sopt.kareer.domain.roadmap.entity.enums.RoadmapActiveStatus.ACTIVE))
                .willReturn(Optional.empty());
        given(memberContextBuilder.load(1L))
                .willReturn(new MemberContextBuilder.MemberAndContext(member, "member-context"));
        given(requiredRetriever.retrieveVisaAll(visa))
                .willThrow(new IllegalStateException("search failure"));

        assertThatThrownBy(() -> roadmapGenerateService.prepareGeneration(
                member,
                visa,
                progress
        )).isInstanceOf(IllegalStateException.class);

        verify(progress).failed(RoadmapGenerationStep.POLICY_SEARCH);
    }

    @Test
    void 로드맵을_재생성하면_기존_활성_Todo를_유지하고_PhaseAction_연결을_해제한다() {
        Roadmap existingRoadmap = Roadmap.builder()
                .id(20L)
                .status(RoadmapActiveStatus.ACTIVE)
                .member(member)
                .build();
        given(member.getId()).willReturn(1L);
        given(roadmapRepository.findByMember_IdAndStatus(1L, RoadmapActiveStatus.ACTIVE))
                .willReturn(Optional.of(existingRoadmap));
        given(memberContextBuilder.load(1L))
                .willReturn(new MemberContextBuilder.MemberAndContext(member, "member-context"));
        given(requiredRetriever.retrieveVisaAll(visa)).willReturn(List.of());
        given(requiredRetriever.retrieveCareer(member))
                .willReturn(new RequiredDocumentRetriever.CareerSelectedDocs(
                        List.of(),
                        List.of(),
                        List.of()
                ));
        given(policyDocumentRetriever.retrievePolicy(member, visa)).willReturn(List.of());

        roadmapGenerateService.prepareGeneration(member, visa, progress);

        verify(actionItemRepository).detachActiveItemsByRoadmapId(20L);
        assertThat(existingRoadmap.getStatus()).isEqualTo(RoadmapActiveStatus.INACTIVE);
    }

}
