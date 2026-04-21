package org.sopt.kareer.domain.member.facade;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.dto.response.MemberCompletionResponse;
import org.sopt.kareer.domain.member.dto.response.MemberInfoResponse;
import org.sopt.kareer.domain.member.dto.response.MemberStatusResponse;
import org.sopt.kareer.domain.member.dto.response.MypageResponse;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.entity.enums.LocalizedOnboardCategoryType;
import org.sopt.kareer.domain.member.service.LocalizedOnboardQueryService;
import org.sopt.kareer.domain.member.service.MemberCommandService;
import org.sopt.kareer.domain.member.service.MemberDeletionService;
import org.sopt.kareer.domain.member.service.MemberQueryService;
import org.sopt.kareer.domain.member.service.dto.request.MypageCommand;
import org.sopt.kareer.domain.member.service.dto.response.MemberCompletion;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberFacade {

    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;
    private final MemberDeletionService memberDeletionService;
    private final LocalizedOnboardQueryService localizedOnboardQueryService;

    public MemberInfoResponse getMemberInfo(Long memberId) {
        Member member = memberQueryService.getMemberById(memberId);
        MemberVisa visa = memberQueryService.getVisaByMemberId(memberId);
        return MemberInfoResponse.of(
                member, visa,
                resolveCountry(member),
                resolvePrimaryMajor(member),
                resolveUniversity(member),
                resolveDegree(member),
                resolveEnglishLevel(member)
        );
    }

    public MemberStatusResponse getMemberStatus(Long memberId) {
        Member member = memberQueryService.getMemberById(memberId);
        MemberVisa visa = memberQueryService.getVisaByMemberId(memberId);
        return MemberStatusResponse.from(member, visa);
    }

    public MypageResponse getMypage(Long memberId) {
        Member member = memberQueryService.getMemberById(memberId);
        MemberVisa visa = memberQueryService.getVisaByMemberId(memberId);
        return MypageResponse.of(
                member, visa,
                resolveCountry(member),
                resolvePrimaryMajor(member),
                resolveUniversity(member),
                resolveDegree(member),
                resolveEnglishLevel(member)
        );
    }

    @Transactional
    public void updateMypage(Long memberId, MypageCommand command) {
        Member member = memberQueryService.getMemberById(memberId);
        MemberVisa visa = memberQueryService.getVisaByMemberId(memberId);
        memberCommandService.updateMypage(member, visa, command);
    }

    public MemberCompletionResponse getCompletion(Long memberId) {
        MemberCompletion completion = memberQueryService.getMemberCompletion(memberId);
        return MemberCompletionResponse.of(completion.onboardingRequired(), completion.agreeTerm());
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberQueryService.getMemberById(memberId);
        memberDeletionService.deleteMember(member);
    }

    private String resolveCountry(Member member) {
        return localizedOnboardQueryService.resolveLabelByCode(LocalizedOnboardCategoryType.COUNTRY, member.getCountryCode());
    }

    private String resolvePrimaryMajor(Member member) {
        return localizedOnboardQueryService.resolveLabelByCode(LocalizedOnboardCategoryType.MAJOR, member.getPrimaryMajorCode());
    }

    private String resolveUniversity(Member member) {
        return localizedOnboardQueryService.resolveLabelByCode(LocalizedOnboardCategoryType.UNIVERSITY, member.getUniversityCode());
    }

    private String resolveDegree(Member member) {
        return localizedOnboardQueryService.resolveLabelByCode(LocalizedOnboardCategoryType.DEGREE, member.getDegreeCode());
    }

    private String resolveEnglishLevel(Member member) {
        return localizedOnboardQueryService.resolveLabelByCode(LocalizedOnboardCategoryType.ENGLISH_LEVEL, member.getEnglishLevelCode());
    }
}
