package org.sopt.kareer.domain.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.enums.OAuthProvider;
import org.sopt.kareer.domain.member.fixture.MemberFixture;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.global.oauth.dto.OAuthAttributes;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceConcurrencyTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberCommandService memberCommandService;

    @DisplayName("OAuth 회원 저장 중 충돌이 발생하면 재조회 결과를 반환한다.")
    @Test
    void retryAfterDataIntegrityViolation() {
        String providerId = UUID.randomUUID().toString();
        OAuthAttributes attributes = new OAuthAttributes(
                OAuthProvider.GOOGLE,
                providerId,
                providerId + "@example.com",
                "tester",
                "profile",
                true,
                Map.of()
        );
        Member existing = MemberFixture.getMember("existing-provider");

        when(memberRepository.findByProviderAndProviderId(attributes.provider(), attributes.providerId()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(existing));
        when(memberRepository.save(any(Member.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        Member result = memberCommandService.findOrCreateByOAuth(attributes);

        assertThat(result).isEqualTo(existing);
        verify(memberRepository, times(2))
                .findByProviderAndProviderId(attributes.provider(), attributes.providerId());
    }
}
