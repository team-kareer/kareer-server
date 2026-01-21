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

    @Enumerated(EnumType.STRING)
    private Country country;

    private String primaryMajor;

    private String secondaryMajor;

    private String targetJob;

    private LocalDate graduationDate;

    private LocalDate expectedGraduationDate;

    private String personalBackground;

    @Enumerated(EnumType.STRING)
    private LanguageLevel languageLevel;

    @Enumerated(EnumType.STRING)
    private Degree degree;

    @Column(length = 1000)
    private String targetJobSkill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoadmapStatus roadmapStatus;

    public void updateInfo(String name,
                           LocalDate birthDate,
                           Country country,
                           LanguageLevel languageLevel,
                           Degree degree,
                           LocalDate expectedGraduationDate,
                           String primaryMajor,
                           String secondaryMajor,
                           String targetJob,
                           String targetJobSkill) {
//        assertPendingStatus();
        this.name = name;
        this.birthDate = birthDate;
        this.country = country;
        this.languageLevel = languageLevel;
        this.degree = degree;
        this.expectedGraduationDate = expectedGraduationDate;
        this.primaryMajor = primaryMajor;
        this.secondaryMajor = secondaryMajor;
        this.targetJob = targetJob;
        this.targetJobSkill = targetJobSkill;
        this.status = MemberStatus.ACTIVE;
    }

    public static Member createOAuthMember(String name,
                                           OAuthProvider provider,
                                           String providerId,
                                           String profileImageUrl) {
        return Member.builder()
                .name(name)
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

    public void assertOnboarded() {
        if (this.status == MemberStatus.PENDING) {
            throw new MemberException(MemberErrorCode.ONBOARDING_REQUIRED);
        }
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
}
