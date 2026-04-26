package org.sopt.kareer.domain.member.facade;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.dto.request.MemberOnboardV2Request;
import org.sopt.kareer.domain.member.dto.request.MemberTermsRequest;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.service.MemberCommandService;
import org.sopt.kareer.domain.member.service.MemberQueryService;
import org.sopt.kareer.domain.term.entity.Term;
import org.sopt.kareer.domain.term.service.TermService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberOnboardingFacade {

    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;
    private final TermService termService;

    @Transactional
    public void onboard(Long memberId, MemberOnboardV2Request request) {
        Member member = memberQueryService.getMemberById(memberId);
        memberCommandService.onboard(member, request);
    }

    @Transactional
    public void agreeTerms(Long memberId, MemberTermsRequest request) {
        Member member = memberQueryService.getMemberById(memberId);
        List<Term> activeTerms = termService.getActiveTerms();
        memberCommandService.agreeTerms(member, activeTerms, request);
    }
}
