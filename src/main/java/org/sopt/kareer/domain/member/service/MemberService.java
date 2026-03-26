package org.sopt.kareer.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.dto.request.MemberOnboardRequest;
import org.sopt.kareer.domain.member.dto.request.MemberOnboardV2Request;
import org.sopt.kareer.domain.member.dto.response.*;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.entity.enums.MemberStatus;
import org.sopt.kareer.domain.member.exception.MemberErrorCode;
import org.sopt.kareer.domain.member.exception.MemberException;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.member.repository.MemberVisaRepository;
import org.sopt.kareer.domain.member.service.dto.request.MypageCommand;
import org.sopt.kareer.domain.member.util.PassportOcrParser;
import org.sopt.kareer.domain.member.util.VisaOcrParser;
import org.sopt.kareer.global.document.service.DocumentProcessingService;
import org.sopt.kareer.global.exception.customexception.GlobalException;
import org.sopt.kareer.global.exception.errorcode.GlobalErrorCode;
import org.sopt.kareer.global.oauth.dto.OAuthAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberVisaRepository memberVisaRepository;
    private final MemberDeletionService memberDeletionService;
    private final DocumentProcessingService documentProcessingService;
    private final VisaOcrParser visaOcrParser;
    private final PassportOcrParser passportOcrParser;

    public Member getById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional
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


    public MemberInfoResponse getMemberInfo(Long memberId) {
        Member member = getById(memberId);
        MemberVisa memberVisa = memberVisaRepository.findActiveByMemberId(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.VISA_NOT_FOUND));
        return MemberInfoResponse.from(member, memberVisa);
    }

    @Transactional
    public void onboardMember(MemberOnboardRequest request, Long memberId) {
        Member member = getById(memberId);
        member.updateInfo(
                request.name(),
                request.birthDate(),
                request.country(),
                null,
                null,
                null,
                null,
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

    @Transactional
    public void onboardMemberV2(MemberOnboardV2Request request, Long memberId) {
        Member member = getById(memberId);
        String fieldOfInterest = String.join(",", request.fieldsOfInterests());
        String preparationStatus = String.join(",", request.preparationStatuses());

        member.updateInfo(
                request.name(),
                request.birthDate(),
                request.country(),
                request.university(),
                request.englishLevel(),
                fieldOfInterest,
                preparationStatus,
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
                .orElseThrow(() -> new MemberException(MemberErrorCode.VISA_NOT_FOUND));

        boolean onboardingRequired = member.getStatus().equals(MemberStatus.PENDING);

        return MemberStatusResponse.from(member, memberVisa, onboardingRequired);
    }

    public MypageResponse getMypage(Long memberId) {
        Member member = getById(memberId);
        MemberVisa memberVisa = memberVisaRepository.findActiveByMemberId(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.VISA_NOT_FOUND));
        return MypageResponse.from(member, memberVisa);
    }

    @Transactional
    public void updateMypage(Long memberId, MypageCommand command) {
        Member member = getById(memberId);
        MemberVisa memberVisa = memberVisaRepository.findActiveByMemberId(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.VISA_NOT_FOUND));

        member.updateProfile(
                command.targetJob(),
                command.birthDate(),
                command.country(),
                command.degree(),
                command.university(),
                command.primaryMajor(),
                command.secondaryMajor(),
                command.languageLevel(),
                command.englishLevel()
        );
        memberVisa.updateVisa(command.visaType(), command.visaExpiredAt());

    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = getById(memberId);
        memberDeletionService.deleteMember(member);
    }


    public OcrVisaResponse getVisaOcr(MultipartFile file) throws IOException {
        String text = documentProcessingService.extractText(file);
        VisaOcrParser.VisaInfo visaInfo = visaOcrParser.parse(text);

        return OcrVisaResponse.from(visaInfo);
    }

    public OcrPassportResponse getPassportOcr(MultipartFile file) throws IOException {
        String text = documentProcessingService.extractText(file);
        PassportOcrParser.PassportInfo passportInfo = passportOcrParser.parse(text);

        return OcrPassportResponse.from(passportInfo);
    }
}
