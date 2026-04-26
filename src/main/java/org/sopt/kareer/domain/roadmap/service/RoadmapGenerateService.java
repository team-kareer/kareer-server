package org.sopt.kareer.domain.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.roadmap.entity.enums.RoadmapActiveStatus;
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
        roadmapRepository.findByMember_IdAndStatus(member.getId(), RoadmapActiveStatus.ACTIVE)
                .ifPresent(existing -> {
                    actionItemRepository.deactivateAllByRoadmapId(existing.getId());
                    existing.deactivate();
                });

        return buildContext(member, visa, false);
    }

    @Transactional(readOnly = true)
    public RoadmapGenerationContext prepareTestGeneration(Member member, MemberVisa visa) {
        return buildContext(member, visa, true);
    }

    private RoadmapGenerationContext buildContext(Member member, MemberVisa visa, boolean requireVisa) {
        var memberContext = memberContextBuilder.load(member.getId());

        List<Document> visaDocs = requireVisa
                ? requiredRetriever.retrieveVisaAll(visa)
                : (visa == null ? List.of() : requiredRetriever.retrieveVisaAll(visa));

        RequiredDocumentRetriever.CareerSelectedDocs careerSelected = requiredRetriever.retrieveCareer(member);
        List<Document> careerDocs = new ArrayList<>();
        careerDocs.addAll(careerSelected.actionRequired());
        careerDocs.addAll(careerSelected.aiGuideRisk());
        careerDocs.addAll(careerSelected.todoList());

        List<Document> policyDocs = policyDocumentRetriever.retrievePolicy(member, visa);

        return new RoadmapGenerationContext(memberContext.contextText(), visaDocs, careerDocs, policyDocs);
    }
}
