package org.sopt.kareer.domain.member.fixture;

import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.enums.*;

import java.time.LocalDate;

public class MemberFixture {
    public static final String MEMBER_NAME = "test-user-name";
    public static final OAuthProvider MEMBER_OAUTH_PROVIDER = OAuthProvider.GOOGLE;
    public static final String MEMBER_PROVIDER_ID = "test-provider-id";
    public static final Country MEMBER_COUNTRY = Country.AFGHANISTAN;
    public static final Degree MEMBER_DEGREE = Degree.DOMESTIC_ASSOCIATE;
    public static final String MEMBER_UNIVERSITY = "University";
    public static final EnglishLevel MEMBER_ENGLISH_LEVEL = EnglishLevel.ADVANCED;



    public static Member getMember() {
        return getMember(MEMBER_PROVIDER_ID);
    }

    public static Member getMember(String providerId){
        return Member.builder()
                .name(MEMBER_NAME)
                .country(MEMBER_COUNTRY)
                .provider(MEMBER_OAUTH_PROVIDER)
                .providerId(providerId)
                .status(MemberStatus.ACTIVE)
                .roadmapStatus(RoadmapStatus.NOT_STARTED)
                .degree(MEMBER_DEGREE)
                .englishLevel(MEMBER_ENGLISH_LEVEL)
                .university(MEMBER_UNIVERSITY)
                .build();
    }

    public static Member getMember(LocalDate birthDate){
        return Member.builder()
                .name(MEMBER_NAME)
                .country(MEMBER_COUNTRY)
                .provider(MEMBER_OAUTH_PROVIDER)
                .providerId(MEMBER_PROVIDER_ID)
                .status(MemberStatus.ACTIVE)
                .roadmapStatus(RoadmapStatus.NOT_STARTED)
                .degree(MEMBER_DEGREE)
                .englishLevel(MEMBER_ENGLISH_LEVEL)
                .university(MEMBER_UNIVERSITY)
                .birthDate(birthDate)
                .build();
    }
}
