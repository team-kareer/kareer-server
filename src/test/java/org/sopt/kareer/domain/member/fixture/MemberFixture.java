package org.sopt.kareer.domain.member.fixture;

import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.enums.Country;
import org.sopt.kareer.domain.member.entity.enums.MemberStatus;
import org.sopt.kareer.domain.member.entity.enums.OAuthProvider;
import org.sopt.kareer.domain.member.entity.enums.RoadmapStatus;

public class MemberFixture {
    public static final String MEMBER_NAME = "test-user-name";
    public static final OAuthProvider MEMBER_OAUTH_PROVIDER = OAuthProvider.GOOGLE;
    public static final String MEMBER_PROVIDER_ID = "test-provider-id";
    public static final Country MEMBER_COUNTRY = Country.AFGHANISTAN;


    public static Member getMember(){
        return Member.builder()
                .name(MEMBER_NAME)
                .country(MEMBER_COUNTRY)
                .provider(MEMBER_OAUTH_PROVIDER)
                .providerId(MEMBER_PROVIDER_ID)
                .status(MemberStatus.ACTIVE)
                .roadmapStatus(RoadmapStatus.NOT_STARTED)
                .build();
    }
}
