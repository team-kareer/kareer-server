package org.sopt.kareer.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.jobposting.repository.JobPostingBookmarkRepository;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.member.repository.MemberVisaRepository;
import org.sopt.kareer.domain.roadmap.repository.ActionItemRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionGuidelineRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionMistakeRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseActionRepository;
import org.sopt.kareer.domain.roadmap.repository.PhaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberDeletionService {

    private final MemberRepository memberRepository;
    private final MemberVisaRepository memberVisaRepository;
    private final JobPostingBookmarkRepository jobPostingBookmarkRepository;
    private final PhaseRepository phaseRepository;
    private final PhaseActionRepository phaseActionRepository;
    private final PhaseActionGuidelineRepository phaseActionGuidelineRepository;
    private final PhaseActionMistakeRepository phaseActionMistakeRepository;
    private final ActionItemRepository actionItemRepository;

    @Transactional
    public void deleteMember(Member member) {
        Long memberId = member.getId();

        phaseActionGuidelineRepository.deleteAllByPhaseAction_Phase_Member_Id(memberId);
        phaseActionMistakeRepository.deleteAllByPhaseAction_Phase_Member_Id(memberId);
        actionItemRepository.deleteAllByMemberId(memberId);
        phaseActionRepository.deleteAllByPhase_Member_Id(memberId);
        phaseRepository.deleteAllByMember_Id(memberId);
        jobPostingBookmarkRepository.deleteAllByMemberId(memberId);
        memberVisaRepository.deleteAllByMemberId(memberId);
        memberRepository.delete(member);
    }
}
