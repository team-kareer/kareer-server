package org.sopt.kareer.global.oauth.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.service.MemberService;
import org.sopt.kareer.global.oauth.dto.OAuthAttributes;
import org.sopt.kareer.global.oauth.principal.CustomOAuth2User;
import org.springframework.dao.DataAccessException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOidcOAuth2UserService extends OidcUserService {

    private final MemberService memberService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        Map<String, Object> attributes = oidcUser.getAttributes();
        OAuthAttributes oauthAttributes = OAuthAttributes.of(
                userRequest.getClientRegistration().getRegistrationId(),
                attributes
        );

        Member member = findOrCreateMember(oauthAttributes);
        OidcUserInfo userInfo = oidcUser.getUserInfo();
        return new CustomOAuth2User(member, attributes, member.getStatus(), oidcUser.getIdToken(), userInfo);
    }

    private Member findOrCreateMember(OAuthAttributes oauthAttributes) {
        try {
            return memberService.findOrCreateByOAuth(oauthAttributes);
        } catch (DataAccessException ex) {
            throw new AuthenticationServiceException("OAuth 회원 정보를 저장하지 못했습니다.", ex);
        }
    }
}
