package org.sopt.kareer.domain.roadmap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.exception.MemberErrorCode;
import org.sopt.kareer.domain.member.exception.MemberException;
import org.sopt.kareer.domain.member.repository.MemberVisaRepository;
import org.sopt.kareer.domain.member.service.MemberService;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapResponse;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapTestResponse;
import org.sopt.kareer.global.external.ai.builder.context.MemberContextBuilder;
import org.sopt.kareer.global.external.ai.service.OpenAiService;
import org.sopt.kareer.global.external.ai.service.PolicyDocumentRetriever;
import org.sopt.kareer.global.external.ai.service.RequiredDocumentRetriever;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoadMapService {

    private final MemberContextBuilder memberContextBuilder;
    private final OpenAiService openAiService;
    private final MemberService memberService;
    private final RoadMapPersistService roadMapPersistService;
    private final RequiredDocumentRetriever requiredRetriever;
    private final PolicyDocumentRetriever policyDocumentRetriever;
    private final MemberVisaRepository memberVisaRepository;

    @Transactional
    public void createRoadmap(Long memberId){
        Member member = memberService.getById(memberId);

        var memberContext = memberContextBuilder.load(memberId);

        MemberVisa visa = memberVisaRepository.findActiveByMemberId(memberId).orElseThrow(() -> new MemberException(MemberErrorCode.VISA_NOT_FOUND));

        List<Document> visaDocs = (visa == null)
                ? List.of()
                : requiredRetriever.retrieveVisaAll(visa);

        RequiredDocumentRetriever.CareerSelectedDocs careerSelected =
                requiredRetriever.retrieveCareer(member);

        List<Document> careerDocs = new ArrayList<>();
        careerDocs.addAll(careerSelected.actionRequired());
        careerDocs.addAll(careerSelected.aiGuideRisk());
        careerDocs.addAll(careerSelected.todoList());

        List<Document> policyDocs = policyDocumentRetriever.retrievePolicy(member, visa);

        RoadmapResponse response = openAiService.generateRoadmap(
                memberContext.contextText(),
                visaDocs,
                careerDocs,
                policyDocs
        );

        roadMapPersistService.saveRoadMap(member, response);

    }

    @Transactional
    public RoadmapTestResponse createRoadmapTest(Long memberId) {
        Long startTime = System.currentTimeMillis();
        Member member = memberService.getById(memberId);

        var memberContext = memberContextBuilder.load(memberId);

        MemberVisa visa = memberVisaRepository.findActiveByMemberId(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.VISA_NOT_FOUND));

        List<Document> visaDocs = requiredRetriever.retrieveVisaAll(visa);

        RequiredDocumentRetriever.CareerSelectedDocs careerSelected =
                requiredRetriever.retrieveCareer(member);

        List<Document> careerDocs = new ArrayList<>();
        careerDocs.addAll(careerSelected.actionRequired());
        careerDocs.addAll(careerSelected.aiGuideRisk());
        careerDocs.addAll(careerSelected.todoList());

        List<Document> policyDocs = policyDocumentRetriever.retrievePolicy(member, visa);

        RoadmapResponse roadmap = openAiService.generateRoadmap(
                memberContext.contextText(),
                visaDocs,
                careerDocs,
                policyDocs
        );

        Long endTime = System.currentTimeMillis();
        log.info("Time : {}ms", (endTime - startTime));

        List<Document> retrieved = new ArrayList<>();
        retrieved.addAll(visaDocs);
        retrieved.addAll(careerDocs);
        retrieved.addAll(policyDocs);

        return RoadmapTestResponse.of(roadmap, retrieved);
    }

}
