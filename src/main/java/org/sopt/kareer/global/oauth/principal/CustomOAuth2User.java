package org.sopt.kareer.global.oauth.principal;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.enums.MemberStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class CustomOAuth2User implements OAuth2User, OidcUser {

    private final Member member;
    private final Map<String, Object> attributes;
    private final MemberStatus status;
    private final Collection<? extends GrantedAuthority> authorities;
    private final OidcIdToken idToken;
    private final OidcUserInfo userInfo;

    public CustomOAuth2User(Member member,
                            Map<String, Object> attributes,
                            MemberStatus status,
                            OidcIdToken idToken,
                            OidcUserInfo userInfo) {
        this.member = member;
        this.attributes = attributes;
        this.status = status;
        this.idToken = idToken;
        this.userInfo = userInfo;
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return String.valueOf(member.getId());
    }

    @Override
    public Map<String, Object> getClaims() {
        return idToken != null ? idToken.getClaims() : attributes;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public OidcIdToken getIdToken() {
        return idToken;
    }
}
