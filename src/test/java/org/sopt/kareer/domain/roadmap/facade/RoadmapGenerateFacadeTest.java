package org.sopt.kareer.domain.roadmap.facade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.service.MemberQueryService;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapResponse;
import org.sopt.kareer.domain.roadmap.dto.translation.RoadmapTranslationTarget;
import org.sopt.kareer.domain.roadmap.progress.RoadmapGenerationStep;
import org.sopt.kareer.domain.roadmap.progress.RoadmapProgressNotifier;
import org.sopt.kareer.domain.roadmap.service.RoadMapPersistService;
import org.sopt.kareer.domain.roadmap.service.RoadmapGenerateService;
import org.sopt.kareer.domain.roadmap.service.RoadmapTranslationPersistService;
import org.sopt.kareer.domain.roadmap.service.dto.response.RoadmapGenerationContext;
import org.sopt.kareer.global.external.ai.service.OpenAiService;
import org.sopt.kareer.global.external.google.service.GoogleTranslationService;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RoadmapGenerateFacadeTest {

    @Mock
    private MemberQueryService memberQueryService;
    @Mock
    private RoadmapGenerateService roadmapGenerateService;
    @Mock
    private OpenAiService openAiService;
    @Mock
    private RoadMapPersistService roadMapPersistService;
    @Mock
    private RoadmapTranslationPersistService roadmapTranslationPersistService;
    @Mock
    private GoogleTranslationService googleTranslationService;
    @Mock
    private ExecutorService executorService;
    @Mock
    private RoadmapProgressNotifier progress;
    @Mock
    private Member member;
    @Mock
    private MemberVisa visa;

    private RoadmapGenerateFacade roadmapGenerateFacade;

    @BeforeEach
    void setUp() {
        roadmapGenerateFacade = new RoadmapGenerateFacade(
                memberQueryService,
                roadmapGenerateService,
                openAiService,
                roadMapPersistService,
                roadmapTranslationPersistService,
                googleTranslationService,
                executorService
        );
    }

    @Test
    void 로드맵_생성_진행_상태를_실제_순서대로_알린다() {
        Long memberId = 1L;
        RoadmapGenerationContext context = new RoadmapGenerationContext(
                "member-context",
                List.of(),
                List.of(),
                List.of()
        );
        RoadmapResponse response = new RoadmapResponse(List.of());
        RoadmapTranslationTarget target = new RoadmapTranslationTarget(List.of());

        given(memberQueryService.getMemberById(memberId)).willReturn(member);
        given(memberQueryService.getVisaByMemberId(memberId)).willReturn(visa);
        given(roadmapGenerateService.prepareGeneration(
                eq(member),
                eq(visa),
                eq(progress)
        )).willAnswer(invocation -> {
            progress.completed(RoadmapGenerationStep.USER_ANALYSIS);
            progress.started(RoadmapGenerationStep.POLICY_SEARCH);
            return context;
        });
        given(openAiService.generateRoadmap(
                "member-context",
                List.of(),
                List.of(),
                List.of()
        )).willReturn(response);
        given(roadMapPersistService.saveRoadMap(member, response)).willReturn(target);

        roadmapGenerateFacade.generateRoadmap(memberId, progress);

        InOrder order = inOrder(progress);
        order.verify(progress).started(RoadmapGenerationStep.USER_ANALYSIS);
        order.verify(progress).completed(RoadmapGenerationStep.USER_ANALYSIS);
        order.verify(progress).started(RoadmapGenerationStep.POLICY_SEARCH);
        order.verify(progress).completed(RoadmapGenerationStep.POLICY_SEARCH);
        order.verify(progress).started(RoadmapGenerationStep.ROADMAP_WRITING);
        order.verify(progress).completed(RoadmapGenerationStep.ROADMAP_WRITING);
    }

    @Test
    void 회원_조회에_실패하면_사용자_분석_실패를_알린다() {
        given(memberQueryService.getMemberById(1L))
                .willThrow(new IllegalStateException("member failure"));

        assertThatThrownBy(() -> roadmapGenerateFacade.generateRoadmap(1L, progress))
                .isInstanceOf(IllegalStateException.class);

        verify(progress).failed(RoadmapGenerationStep.USER_ANALYSIS);
    }

    @Test
    void OpenAI_호출에_실패하면_로드맵_작성_실패를_알린다() {
        RoadmapGenerationContext context = new RoadmapGenerationContext(
                "member-context",
                List.of(),
                List.of(),
                List.of()
        );

        given(memberQueryService.getMemberById(1L)).willReturn(member);
        given(memberQueryService.getVisaByMemberId(1L)).willReturn(visa);
        given(roadmapGenerateService.prepareGeneration(member, visa, progress))
                .willReturn(context);
        given(openAiService.generateRoadmap(
                "member-context",
                List.of(),
                List.of(),
                List.of()
        )).willThrow(new IllegalStateException("openai failure"));

        assertThatThrownBy(() -> roadmapGenerateFacade.generateRoadmap(1L, progress))
                .isInstanceOf(IllegalStateException.class);

        verify(progress).failed(RoadmapGenerationStep.ROADMAP_WRITING);
    }

}
