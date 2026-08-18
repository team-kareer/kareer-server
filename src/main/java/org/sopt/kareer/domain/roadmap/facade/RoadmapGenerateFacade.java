package org.sopt.kareer.domain.roadmap.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.service.MemberQueryService;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapResponse;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapTestResponse;
import org.sopt.kareer.domain.roadmap.dto.translation.RoadmapTranslationTarget;
import org.sopt.kareer.domain.roadmap.progress.NoOpRoadmapProgressNotifier;
import org.sopt.kareer.domain.roadmap.progress.RoadmapGenerationStep;
import org.sopt.kareer.domain.roadmap.progress.RoadmapProgressNotifier;
import org.sopt.kareer.domain.roadmap.service.RoadMapPersistService;
import org.sopt.kareer.domain.roadmap.service.RoadmapGenerateService;
import org.sopt.kareer.domain.roadmap.service.RoadmapTranslationPersistService;
import org.sopt.kareer.domain.roadmap.service.dto.response.RoadmapGenerationContext;
import org.sopt.kareer.global.external.ai.service.OpenAiService;
import org.sopt.kareer.global.external.google.service.GoogleTranslationService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoadmapGenerateFacade {

    private static final List<String> TARGET_LANGUAGES = List.of("en", "vi", "zh-CN");

    private final MemberQueryService memberQueryService;
    private final RoadmapGenerateService roadmapGenerateService;
    private final OpenAiService openAiService;
    private final RoadMapPersistService roadMapPersistService;
    private final RoadmapTranslationPersistService roadmapTranslationPersistService;
    private final GoogleTranslationService googleTranslationService;
    private final ExecutorService executorService;

    public void generateRoadmap(Long memberId) {
        generateRoadmap(memberId, NoOpRoadmapProgressNotifier.INSTANCE);
    }

    public void generateRoadmap(Long memberId, RoadmapProgressNotifier progressNotifier) {
        progressNotifier.started(RoadmapGenerationStep.USER_ANALYSIS);

        Member member;
        MemberVisa visa;
        try {
            member = memberQueryService.getMemberById(memberId);
            visa = memberQueryService.getVisaByMemberId(memberId);
        } catch (RuntimeException exception) {
            progressNotifier.failed(RoadmapGenerationStep.USER_ANALYSIS);
            throw exception;
        }

        RoadmapGenerationContext context = roadmapGenerateService.prepareGeneration(member, visa, progressNotifier);
        progressNotifier.completed(RoadmapGenerationStep.POLICY_SEARCH);
        progressNotifier.started(RoadmapGenerationStep.ROADMAP_WRITING);

        RoadmapTranslationTarget target;
        try {
            RoadmapResponse response = openAiService.generateRoadmap(
                    context.memberContextText(),
                    context.visaDocs(),
                    context.careerDocs(),
                    context.policyDocs()
            );

            target = roadMapPersistService.saveRoadMap(member, response);
            progressNotifier.completed(RoadmapGenerationStep.ROADMAP_WRITING);
        } catch (RuntimeException exception) {
            progressNotifier.failed(RoadmapGenerationStep.ROADMAP_WRITING);
            throw exception;
        }

        translateAllLanguages(target);
    }

    public RoadmapTestResponse generateRoadmapForTest(Long memberId) {
        Member member = memberQueryService.getMemberById(memberId);
        MemberVisa visa = memberQueryService.getVisaByMemberId(memberId);

        RoadmapGenerationContext context = roadmapGenerateService.prepareTestGeneration(member, visa);

        RoadmapResponse roadmap = openAiService.generateRoadmap(
                context.memberContextText(),
                context.visaDocs(),
                context.careerDocs(),
                context.policyDocs()
        );

        return RoadmapTestResponse.of(roadmap, context.allDocs());
    }

    private void translateAllLanguages(RoadmapTranslationTarget target) {
        if (target.phases().isEmpty()) return;

        List<CompletableFuture<Void>> futures = TARGET_LANGUAGES.stream()
                .map(language -> CompletableFuture.runAsync(() -> {
                    try {
                        RoadmapTranslationTarget translated = googleTranslationService.translate(target, language);
                        roadmapTranslationPersistService.saveTranslations(translated, language);
                    } catch (Exception e) {
                        log.error("[TRANSLATION] failed: language={}", language, e);
                    }
                }, executorService))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
}
