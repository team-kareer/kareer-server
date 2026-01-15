package org.sopt.kareer.domain.roadmap.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.exception.MemberErrorCode;
import org.sopt.kareer.domain.member.exception.MemberException;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.roadmap.dto.response.AiGuideResponse;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseResponse;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseListResponse;
import org.sopt.kareer.domain.roadmap.entity.PhaseAction;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;
import org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionGuidelineRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionMistakeRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhaseService {

    private final MemberRepository memberRepository;
    private final PhaseRepository phaseRepository;
    private final PhaseActionRepository phaseActionRepository;
    private final PhaseActionMistakeRepository mistakeRepository;
    private final PhaseActionGuidelineRepository guidelineRepository;

    public PhaseListResponse getPhases(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        List<PhaseResponse> responses= phaseRepository.findPhases(memberId);

        return PhaseListResponse.from(responses);
    }

    public AiGuideResponse getAiGuide(Long phaseActionId) {
        PhaseAction phaseAction = phaseActionRepository.findById(phaseActionId).
                orElseThrow(() -> new RoadMapException(RoadmapErrorCode.PHASE_ACTION_NOT_FOUND));

        List<String> mistakes = mistakeRepository.findContentByPhaseActionId(phaseActionId);
        List<String> guidelines = guidelineRepository.findContentByPhaseActionId(phaseActionId);


        return AiGuideResponse.from(phaseAction, mistakes, guidelines);
    }
}
