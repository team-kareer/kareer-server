package org.sopt.kareer.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.dto.request.*;
import org.sopt.kareer.domain.member.dto.response.*;
import org.sopt.kareer.domain.member.entity.*;
import org.sopt.kareer.domain.member.entity.enums.*;
import org.sopt.kareer.domain.member.exception.*;
import org.sopt.kareer.domain.member.repository.*;
import org.sopt.kareer.domain.member.service.dto.request.MypageCommand;
import org.sopt.kareer.domain.member.util.*;
import org.sopt.kareer.global.document.exception.*;
import org.sopt.kareer.global.document.service.DocumentProcessingService;
import org.sopt.kareer.global.exception.customexception.GlobalException;
import org.sopt.kareer.global.exception.errorcode.GlobalErrorCode;
import org.sopt.kareer.global.oauth.dto.OAuthAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberVisaRepository memberVisaRepository;
    private final MemberDeletionService memberDeletionService;
    private final LocalizedOnboardQueryService localizedOnboardQueryService;
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
        String countryLabel = localizedOnboardQueryService.resolveLabelByCode(LocalizedOnboardCategoryType.COUNTRY,
                member.getCountryCode());
        String primaryMajorLabel = localizedOnboardQueryService.resolveLabelByCode(LocalizedOnboardCategoryType.MAJOR,
                member.getPrimaryMajorCode());
        String universityLabel = localizedOnboardQueryService.resolveLabelByCode(
                LocalizedOnboardCategoryType.UNIVERSITY, member.getUniversityCode());
        String degreeLabel = localizedOnboardQueryService.resolveLabelByCode(LocalizedOnboardCategoryType.DEGREE,
                member.getDegreeCode());
        String englishLevelLabel = localizedOnboardQueryService.resolveLabelByCode(
                LocalizedOnboardCategoryType.ENGLISH_LEVEL, member.getEnglishLevelCode());

        return MemberInfoResponse.of(
                member,
                memberVisa,
                countryLabel,
                primaryMajorLabel,
                universityLabel,
                degreeLabel,
                englishLevelLabel
        );
    }

    // 프론트 온보딩 구현 완료 후 삭제 예정
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
                request.targetJobSkill(),
                request.personalBackground()
        );

        MemberVisa memberVisa = MemberVisa.createMemberVisa(
                member,
                request.visaType(),
                request.visaExpiredAt(),
                request.visaStartDate()
        );
        memberVisaRepository.save(memberVisa);
    }

    @Transactional
    public void onboardMemberV2(MemberOnboardV2Request request, Long memberId) {
        Member member = getById(memberId);
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

        MemberVisa memberVisa = MemberVisa.createMemberVisa(
                member,
                request.visaType(),
                request.visaExpiredAt(),
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
        String countryLabel = localizedOnboardQueryService.resolveLabelByCode(LocalizedOnboardCategoryType.COUNTRY,
                member.getCountryCode());
        String primaryMajorLabel = localizedOnboardQueryService.resolveLabelByCode(LocalizedOnboardCategoryType.MAJOR,
                member.getPrimaryMajorCode());
        String universityLabel = localizedOnboardQueryService.resolveLabelByCode(
                LocalizedOnboardCategoryType.UNIVERSITY, member.getUniversityCode());
        String degreeLabel = localizedOnboardQueryService.resolveLabelByCode(LocalizedOnboardCategoryType.DEGREE,
                member.getDegreeCode());
        String englishLevelLabel = localizedOnboardQueryService.resolveLabelByCode(
                LocalizedOnboardCategoryType.ENGLISH_LEVEL, member.getEnglishLevelCode());

        return MypageResponse.of(member, memberVisa, countryLabel, primaryMajorLabel, universityLabel, degreeLabel,
                englishLevelLabel);

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


    public OcrVisaResponse getVisaOcr(MultipartFile file) {
        try {
            String text = documentProcessingService.extractText(file);
            VisaOcrParser.VisaInfo visaInfo = visaOcrParser.parse(text);

            return OcrVisaResponse.from(visaInfo);
        } catch (DocumentException e) {
            throw e;
        } catch (Exception e) {
            throw new DocumentException(
                    DocumentErrorCode.OCR_PROCESSING_FAILED
            );
        }
    }

    public OcrPassportResponse getPassportOcr(MultipartFile file) {
        try {
            String text = documentProcessingService.extractText(file);
            PassportOcrParser.PassportInfo passportInfo = passportOcrParser.parse(text);

            return OcrPassportResponse.from(passportInfo);
        } catch (DocumentException e) {
            throw e;
        } catch (Exception e) {
            throw new DocumentException(
                    DocumentErrorCode.OCR_PROCESSING_FAILED
            );
        }
    }

}
