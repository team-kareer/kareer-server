package org.sopt.kareer.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sopt.kareer.domain.member.entity.enums.*;
import org.sopt.kareer.domain.member.exception.MemberErrorCode;
import org.sopt.kareer.domain.member.exception.MemberException;
import org.sopt.kareer.global.entity.BaseEntity;

import java.time.LocalDate;

@Table(name = "members", uniqueConstraints = {
        @UniqueConstraint(name = "uk_member_provider_provider_id", columnNames = {"provider", "provider_id"})
})
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 320)
    private String email;

    private String profileImageUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OAuthProvider provider;

    @Column(nullable = false)
    private String providerId;

    private LocalDate birthDate;

    private String countryCode;

    private String primaryMajorCode;

    private String secondaryMajor;

    private String targetJob;

    private LocalDate graduationDate;

    private LocalDate expectedGraduationDate;

    private String personalBackground;

    private String universityCode;

    @Enumerated(EnumType.STRING)
    private EnglishLevel englishLevel;

    @Enumerated(EnumType.STRING)
    private LanguageLevel languageLevel;

    @Enumerated(EnumType.STRING)
    private Degree degree;

    @Column(length = 1000)
    private String targetJobSkill;

    private String preparationStatus;

    private String fieldsOfInterest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoadmapStatus roadmapStatus;

    public void updateInfo(String name,
                           LocalDate birthDate,
                           Country country,
                           String university,
                           EnglishLevel englishLevel,
                           String fieldsOfInterests,
                           String preparationStatuses,
                           LanguageLevel languageLevel,
                           Degree degree,
                           LocalDate expectedGraduationDate,
                           String primaryMajor,
                           String secondaryMajor,
                           String targetJob,
                           String targetJobSkill,
                           String personalBackground) {
        assertPendingStatus();
        this.name = name;
        this.birthDate = birthDate;
        this.countryCode = country.getCountryName();
        this.universityCode = university;
        this.englishLevel = englishLevel;
        this.fieldsOfInterest = fieldsOfInterests;
        this.preparationStatus = preparationStatuses;
        this.languageLevel = languageLevel;
        this.degree = degree;
        this.expectedGraduationDate = expectedGraduationDate;
        this.primaryMajorCode = primaryMajor;
        this.secondaryMajor = secondaryMajor;
        this.targetJob = targetJob;
        this.targetJobSkill = targetJobSkill;
        this.status = MemberStatus.ACTIVE;
        this.personalBackground = personalBackground;
    }

    public void updateInfoV2(String name,
                           LocalDate birthDate,
                           String countryCode,
                           String universityCode,
                           EnglishLevel englishLevel,
                           String fieldsOfInterests,
                           String preparationStatuses,
                           LanguageLevel languageLevel,
                           Degree degree,
                           LocalDate expectedGraduationDate,
                           String primaryMajorCode,
                           String secondaryMajor,
                           String targetJob,
                           String targetJobSkill,
                           String personalBackground) {
        assertPendingStatus();
        this.name = name;
        this.birthDate = birthDate;
        this.countryCode = countryCode;
        this.universityCode = universityCode;
        this.englishLevel = englishLevel;
        this.fieldsOfInterest = fieldsOfInterests;
        this.preparationStatus = preparationStatuses;
        this.languageLevel = languageLevel;
        this.degree = degree;
        this.expectedGraduationDate = expectedGraduationDate;
        this.primaryMajorCode = primaryMajorCode;
        this.secondaryMajor = secondaryMajor;
        this.targetJob = targetJob;
        this.targetJobSkill = targetJobSkill;
        this.status = MemberStatus.ACTIVE;
        this.personalBackground = personalBackground;
    }

    public static Member createOAuthMember(String name,
                                           OAuthProvider provider,
                                           String providerId,
                                           String profileImageUrl,
                                           String email) {
        return Member.builder()
                .name(name)
                .email(email)
                .status(MemberStatus.PENDING)
                .provider(provider)
                .providerId(providerId)
                .profileImageUrl(profileImageUrl)
                .roadmapStatus(RoadmapStatus.NOT_STARTED)
                .build();
    }

    public void updateOAuthProfile(String name, String profileImageUrl) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    private void assertPendingStatus() {
        if (this.status == MemberStatus.ACTIVE) {
            throw new MemberException(MemberErrorCode.ONBOARDING_ALREADY_COMPLETED);
        }
    }


    public void assertCanStartRoadmap() {
        if (roadmapStatus == RoadmapStatus.IN_PROGRESS) {
            throw new MemberException(MemberErrorCode.ROADMAP_IN_PROGRESS);
        }
        if (roadmapStatus == RoadmapStatus.DONE) {
            throw new MemberException(MemberErrorCode.ROADMAP_ALREADY_GENERATED);
        }
    }

    public void markRoadmapInProgress() { this.roadmapStatus = RoadmapStatus.IN_PROGRESS; }
    public void markRoadmapDone() { this.roadmapStatus = RoadmapStatus.DONE; }
    public void markRoadmapFailed() { this.roadmapStatus = RoadmapStatus.FAILED; }

    public void updateProfile(
            String targetJob,
            LocalDate birthDate,
            Country country,
            Degree degree,
            String university,
            String primaryMajor,
            String secondaryMajor,
            LanguageLevel languageLevel,
            EnglishLevel englishLevel
    ) {
        this.targetJob = targetJob;
        this.birthDate = birthDate;
        this.countryCode = country.getCountryName();
        this.degree = degree;
        this.universityCode = university;
        this.primaryMajorCode = primaryMajor;
        this.secondaryMajor = secondaryMajor;
        this.languageLevel = languageLevel;
        this.englishLevel = englishLevel;
    }
}
