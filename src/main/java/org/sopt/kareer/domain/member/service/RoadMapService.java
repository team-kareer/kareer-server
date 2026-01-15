package org.sopt.kareer.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.dto.response.RoadmapResponse;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.util.MemberContextBuilder;
import org.sopt.kareer.global.external.ai.service.OpenAiService;
import org.sopt.kareer.global.external.ai.service.RagService;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoadMapService {

    private final MemberContextBuilder memberContextBuilder;
    private final RagService documentService;
    private final OpenAiService openAiService;
    private final MemberService memberService;
    private final RoadMapPersistService roadMapPersistService;

    @Transactional
    public RoadmapResponse createRoadmap(Long memberId){
        Member member = memberService.getById(memberId);

        var context = memberContextBuilder.load(memberId);

        List<Document> searchedDocs = documentService.search(context.contextText(), 5);

        RoadmapResponse response = openAiService.generateRoadmap(context.contextText(), searchedDocs);
        roadMapPersistService.saveRoadMap(member, response);

        return response;

    }


}
