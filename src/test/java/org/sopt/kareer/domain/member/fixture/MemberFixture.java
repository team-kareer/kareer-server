package org.sopt.kareer.domain.member.fixture;

import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.enums.*;

import java.time.LocalDate;

public class MemberFixture {
    public static final String MEMBER_NAME = "test-user-name";
    public static final OAuthProvider MEMBER_OAUTH_PROVIDER = OAuthProvider.GOOGLE;
    public static final String MEMBER_PROVIDER_ID = "test-provider-id";
    public static final String MEMBER_EMAIL = "member@example.com";
    public static final String MEMBER_COUNTRY_CODE = "afghanistan";
    public static final String MEMBER_DEGREE_CODE = "south-korea-associate";
    public static final String MEMBER_UNIVERSITY_CODE = "konkuk-university";
    public static final String MEMBER_ENGLISH_LEVEL_CODE = "advanced";


    public static Member getMember() {
        return getMember(MEMBER_PROVIDER_ID);
    }

    public static Member getMember(String providerId) {
        return Member.builder()
                .name(MEMBER_NAME)
                .email(MEMBER_EMAIL)
                .countryCode(MEMBER_COUNTRY_CODE)
                .provider(MEMBER_OAUTH_PROVIDER)
                .providerId(providerId)
                .status(MemberStatus.ACTIVE)
                .degreeCode(MEMBER_DEGREE_CODE)
                .englishLevelCode(MEMBER_ENGLISH_LEVEL_CODE)
                .universityCode(MEMBER_UNIVERSITY_CODE)
                .build();
    }

    public static Member getMember(LocalDate birthDate) {
        return Member.builder()
                .name(MEMBER_NAME)
                .email(MEMBER_EMAIL)
                .countryCode(MEMBER_COUNTRY_CODE)
                .provider(MEMBER_OAUTH_PROVIDER)
                .providerId(MEMBER_PROVIDER_ID)
                .status(MemberStatus.ACTIVE)
                .degreeCode(MEMBER_DEGREE_CODE)
                .englishLevelCode(MEMBER_ENGLISH_LEVEL_CODE)
                .universityCode(MEMBER_UNIVERSITY_CODE)
                .birthDate(birthDate)
                .build();
    }
}
