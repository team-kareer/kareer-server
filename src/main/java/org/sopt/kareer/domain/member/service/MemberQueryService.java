package org.sopt.kareer.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.entity.enums.MemberStatus;
import org.sopt.kareer.domain.member.exception.MemberErrorCode;
import org.sopt.kareer.domain.member.exception.MemberException;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.member.repository.MemberTermRepository;
import org.sopt.kareer.domain.member.repository.MemberVisaRepository;
import org.sopt.kareer.domain.member.service.dto.response.MemberCompletion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberRepository memberRepository;

    private final MemberVisaRepository memberVisaRepository;

    private final MemberTermRepository memberTermRepository;

    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public MemberVisa getVisaByMemberId(Long memberId){
        return memberVisaRepository.findActiveByMemberId(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.VISA_NOT_FOUND));
    }

    public MemberCompletion getMemberCompletion(Long memberId){
        Member member = getMemberById(memberId);

        boolean onboardingRequired = member.getStatus().equals(MemberStatus.PENDING);
        boolean agreedTerm = memberTermRepository.existsByMemberIdAndAgreedTrue(memberId);

        return MemberCompletion.of(onboardingRequired, agreedTerm);

    }

}
