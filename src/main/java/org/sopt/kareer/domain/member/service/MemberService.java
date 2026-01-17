package org.sopt.kareer.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.dto.request.MemberOnboardRequest;
import org.sopt.kareer.domain.member.dto.response.MemberInfoResponse;
import org.sopt.kareer.domain.member.dto.response.MemberStatusResponse;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.exception.MemberErrorCode;
import org.sopt.kareer.domain.member.exception.MemberException;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.member.repository.MemberVisaRepository;
import org.sopt.kareer.global.exception.customexception.GlobalException;
import org.sopt.kareer.global.exception.errorcode.GlobalErrorCode;
import org.sopt.kareer.global.oauth.dto.OAuthAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberVisaRepository memberVisaRepository;

    public Member getById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional
    public Member upsertByOAuth(OAuthAttributes attributes) {
        return memberRepository.findByProviderAndProviderId(attributes.provider(), attributes.providerId())
                .map(existing -> {
                    existing.updateOAuthProfile(attributes.name(), attributes.picture());
                    return existing;
                })
                .orElseGet(() -> createNewMember(attributes));
    }

    private Member createNewMember(OAuthAttributes attributes) {
        Member member = Member.createOAuthMember(
                attributes.name(),
                attributes.provider(),
                attributes.providerId(),
                attributes.picture()
        );
        try {
            return memberRepository.save(member);
        } catch (DataIntegrityViolationException ex) {
            return memberRepository.findByProviderAndProviderId(attributes.provider(), attributes.providerId())
                    .orElseThrow(() -> new GlobalException(GlobalErrorCode.INTERNAL_SERVER_ERROR));
        }
    }


    public MemberInfoResponse getMemberInfo(Long memberId) {
        Member member = getById(memberId);
        member.assertOnboarded();
        return MemberInfoResponse.fromEntity(member);
    }

    @Transactional
    public void onboardMember(MemberOnboardRequest request, Long memberId) {
        Member member = getById(memberId);
        member.updateInfo(
                request.name(),
                request.birthDate(),
                request.country(),
                request.languageLevel(),
                request.degree(),
                request.expectedGraduationDate(),
                request.primaryMajor(),
                request.secondaryMajor(),
                request.targetJob(),
                request.targetJobSkill()
        );

        MemberVisa memberVisa = MemberVisa.createMemberVisa(
                member,
                request.visaType(),
                request.visaExpiredAt(),
                request.visaPoint(),
                request.visaStartDate()
        );
        memberVisaRepository.save(memberVisa);
    }

    public MemberStatusResponse getMemberStatus(Long memberId) {
        Member member = getById(memberId);

        MemberVisa memberVisa = memberVisaRepository.findActiveByMemberId(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return MemberStatusResponse.from(member, memberVisa );
    }
}
