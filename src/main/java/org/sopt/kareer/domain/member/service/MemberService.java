package org.sopt.kareer.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.global.exception.customexception.InternalServerException;
import org.sopt.kareer.global.exception.customexception.NotFoundException;
import org.sopt.kareer.global.exception.errorcode.GlobalErrorCode;
import org.sopt.kareer.global.exception.errorcode.MemberErrorCode;
import org.sopt.kareer.global.oauth.dto.OAuthAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public Member getById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
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
                    .orElseThrow(() -> new InternalServerException(GlobalErrorCode.INTERNAL_SERVER_ERROR));
        }
    }
}
