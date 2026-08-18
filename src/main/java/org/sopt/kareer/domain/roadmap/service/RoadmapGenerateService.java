package org.sopt.kareer.domain.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.roadmap.entity.enums.RoadmapActiveStatus;
import org.sopt.kareer.domain.roadmap.progress.NoOpRoadmapProgressNotifier;
import org.sopt.kareer.domain.roadmap.progress.RoadmapGenerationStep;
import org.sopt.kareer.domain.roadmap.progress.RoadmapProgressNotifier;
import org.sopt.kareer.domain.roadmap.repository.ActionItemRepository;
import org.sopt.kareer.domain.roadmap.repository.RoadmapRepository;
import org.sopt.kareer.domain.roadmap.service.dto.response.RoadmapGenerationContext;
import org.sopt.kareer.global.external.ai.builder.context.MemberContextBuilder;
import org.sopt.kareer.global.external.ai.service.PolicyDocumentRetriever;
import org.sopt.kareer.global.external.ai.service.RequiredDocumentRetriever;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoadmapGenerateService {

    private final MemberContextBuilder memberContextBuilder;
    private final RequiredDocumentRetriever requiredRetriever;
    private final PolicyDocumentRetriever policyDocumentRetriever;
    private final RoadmapRepository roadmapRepository;
    private final ActionItemRepository actionItemRepository;

    @Transactional
    public RoadmapGenerationContext prepareGeneration(Member member, MemberVisa visa) {
        return prepareGeneration(member, visa, NoOpRoadmapProgressNotifier.INSTANCE);
    }

    @Transactional
    public RoadmapGenerationContext prepareGeneration(
            Member member,
            MemberVisa visa,
            RoadmapProgressNotifier progressNotifier
    ) {
        RoadmapGenerationStep currentStep = RoadmapGenerationStep.USER_ANALYSIS;

        try {
            roadmapRepository.findByMember_IdAndStatus(member.getId(), RoadmapActiveStatus.ACTIVE)
                    .ifPresent(existing -> {
                        actionItemRepository.deactivateAllByRoadmapId(existing.getId());
                        existing.deactivate();
                    });

            var memberContext = memberContextBuilder.load(member.getId());
            progressNotifier.completed(RoadmapGenerationStep.USER_ANALYSIS);

            currentStep = RoadmapGenerationStep.POLICY_SEARCH;
            progressNotifier.started(currentStep);

            return buildContext(member, visa, false, memberContext.contextText());
        } catch (RuntimeException exception) {
            progressNotifier.failed(currentStep);
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public RoadmapGenerationContext prepareTestGeneration(Member member, MemberVisa visa) {
        var memberContext = memberContextBuilder.load(member.getId());
        return buildContext(member, visa, true, memberContext.contextText());
    }

    private RoadmapGenerationContext buildContext(
            Member member,
            MemberVisa visa,
            boolean requireVisa,
            String memberContextText
    ) {
        List<Document> visaDocs = requireVisa
                ? requiredRetriever.retrieveVisaAll(visa)
                : (visa == null ? List.of() : requiredRetriever.retrieveVisaAll(visa));

        RequiredDocumentRetriever.CareerSelectedDocs careerSelected = requiredRetriever.retrieveCareer(member);
        List<Document> careerDocs = new ArrayList<>();
        careerDocs.addAll(careerSelected.actionRequired());
        careerDocs.addAll(careerSelected.aiGuideRisk());
        careerDocs.addAll(careerSelected.todoList());

        List<Document> policyDocs = policyDocumentRetriever.retrievePolicy(member, visa);

        return new RoadmapGenerationContext(memberContextText, visaDocs, careerDocs, policyDocs);
    }
}
