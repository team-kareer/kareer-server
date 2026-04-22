package org.sopt.kareer.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.dto.request.MemberOnboardV2Request;
import org.sopt.kareer.domain.member.dto.request.MemberTermsRequest;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberTerm;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.member.repository.MemberTermRepository;
import org.sopt.kareer.domain.member.repository.MemberVisaRepository;
import org.sopt.kareer.domain.member.service.dto.request.MypageCommand;
import org.sopt.kareer.domain.term.entity.Term;
import org.sopt.kareer.domain.term.exception.TermErrorCode;
import org.sopt.kareer.domain.term.exception.TermException;
import org.sopt.kareer.global.exception.customexception.GlobalException;
import org.sopt.kareer.global.exception.errorcode.GlobalErrorCode;
import org.sopt.kareer.global.oauth.dto.OAuthAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final MemberVisaRepository memberVisaRepository;
    private final MemberTermRepository memberTermRepository;

    public Member findOrCreateByOAuth(OAuthAttributes attributes) {
        return memberRepository.findByProviderAndProviderId(attributes.provider(), attributes.providerId())
                .orElseGet(() -> createNewMember(attributes));
    }

    private Member createNewMember(OAuthAttributes attributes) {
        Member member = Member.createOAuthMember(
                attributes.name(),
                attributes.provider(),
                attributes.providerId(),
                attributes.picture(),
                attributes.email()
        );
        try {
            return memberRepository.save(member);
        } catch (DataIntegrityViolationException ex) {
            return memberRepository.findByProviderAndProviderId(attributes.provider(), attributes.providerId())
                    .orElseThrow(() -> new GlobalException(GlobalErrorCode.INTERNAL_SERVER_ERROR));
        }
    }

    public void onboard(Member member, MemberOnboardV2Request request) {
        String fieldOfInterest = String.join(",", request.fieldsOfInterests());
        String preparationStatus = String.join(",", request.preparationStatuses());

        member.updateInfoV2(
                request.name(),
                request.birthDate(),
                request.countryCode(),
                request.universityCode(),
                request.englishLevel().getDescription(),
                fieldOfInterest,
                preparationStatus,
                request.languageLevel(),
                request.degree().getDescription(),
                request.expectedGraduationDate(),
                request.primaryMajorCode(),
                request.secondaryMajor(),
                request.targetJob(),
                request.targetJobSkill(),
                request.personalBackground()
        );
        Member managedMember = memberRepository.save(member);

        memberVisaRepository.save(MemberVisa.createMemberVisa(
                managedMember,
                request.visaType(),
                request.visaExpiredAt(),
                request.visaStartDate()
        ));
    }

    public void updateMypage(Member member, MemberVisa visa, MypageCommand command) {
        member.updateProfile(
                command.targetJob(),
                command.birthDate(),
                command.countryCode(),
                command.degree(),
                command.universityCode(),
                command.primaryMajorCode(),
                command.secondaryMajor(),
                command.languageLevel(),
                command.englishLevel()
        );
        visa.updateVisa(command.visaType(), command.visaExpiredAt());
        memberRepository.save(member);
        memberVisaRepository.save(visa);
    }

    public void agreeTerms(Member member, List<Term> activeTerms, MemberTermsRequest request) {
        if (memberTermRepository.existsByMemberId(member.getId())) {
            throw new TermException(TermErrorCode.ALREADY_AGREED_TERMS);
        }

        Map<Long, Boolean> agreementMap = request.agreements().stream()
                .collect(Collectors.toMap(
                        MemberTermsRequest.TermAgreement::termId,
                        MemberTermsRequest.TermAgreement::agreed,
                        (existing, replacement) -> {
                            throw new TermException(TermErrorCode.DUPLICATE_TERM);
                        }
                ));

        Set<Long> activeTermIds = activeTerms.stream()
                .map(Term::getId)
                .collect(Collectors.toSet());

        if (!activeTermIds.equals(agreementMap.keySet())) {
            throw new TermException(TermErrorCode.MISSING_TERM);
        }

        List<MemberTerm> memberTerms = activeTerms.stream()
                .map(term -> {
                    Boolean agreed = agreementMap.get(term.getId());
                    if (term.isRequired() && !Boolean.TRUE.equals(agreed)) {
                        throw new TermException(TermErrorCode.REQUIRED_TERM_NOT_AGREED);
                    }
                    return MemberTerm.create(agreed, member, term);
                })
                .toList();

        memberTermRepository.saveAll(memberTerms);
    }
}
