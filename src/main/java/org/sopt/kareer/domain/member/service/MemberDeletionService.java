package org.sopt.kareer.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.jobposting.repository.JobPostingBookmarkRepository;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.member.repository.MemberTermRepository;
import org.sopt.kareer.domain.member.repository.MemberVisaRepository;
import org.sopt.kareer.domain.roadmap.repository.ActionItemRepository;
import org.sopt.kareer.domain.roadmap.repository.ActionItemTranslationRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionGuidelineRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionGuidelineTranslationRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionMistakeRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionMistakeTranslationRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionTranslationRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseTranslationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberDeletionService {

    private final MemberRepository memberRepository;
    private final MemberTermRepository memberTermRepository;
    private final MemberVisaRepository memberVisaRepository;
    private final JobPostingBookmarkRepository jobPostingBookmarkRepository;
    private final PhaseRepository phaseRepository;
    private final PhaseTranslationRepository phaseTranslationRepository;
    private final PhaseActionRepository phaseActionRepository;
    private final PhaseActionTranslationRepository phaseActionTranslationRepository;
    private final PhaseActionGuidelineRepository phaseActionGuidelineRepository;
    private final PhaseActionGuidelineTranslationRepository phaseActionGuidelineTranslationRepository;
    private final PhaseActionMistakeRepository phaseActionMistakeRepository;
    private final PhaseActionMistakeTranslationRepository phaseActionMistakeTranslationRepository;
    private final ActionItemRepository actionItemRepository;
    private final ActionItemTranslationRepository actionItemTranslationRepository;

    @Transactional
    public void deleteMember(Member member) {
        Long memberId = member.getId();

        phaseActionGuidelineTranslationRepository.deleteAllByGuideline_PhaseAction_Phase_Member_Id(memberId);
        phaseActionMistakeTranslationRepository.deleteAllByMistake_PhaseAction_Phase_Member_Id(memberId);
        actionItemTranslationRepository.deleteAllByActionItem_Member_Id(memberId);
        phaseActionTranslationRepository.deleteAllByPhaseAction_Phase_Member_Id(memberId);
        phaseTranslationRepository.deleteAllByPhase_Member_Id(memberId);

        phaseActionGuidelineRepository.deleteAllByPhaseAction_Phase_Member_Id(memberId);
        phaseActionMistakeRepository.deleteAllByPhaseAction_Phase_Member_Id(memberId);
        actionItemRepository.deleteAllByMemberId(memberId);
        phaseActionRepository.deleteAllByPhase_Member_Id(memberId);
        phaseRepository.deleteAllByMember_Id(memberId);
        jobPostingBookmarkRepository.deleteAllByMemberId(memberId);
        memberTermRepository.deleteAllByMemberId(memberId);
        memberVisaRepository.deleteAllByMemberId(memberId);
        memberRepository.delete(member);
    }
}
