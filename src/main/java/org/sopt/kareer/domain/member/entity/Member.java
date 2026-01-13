package org.sopt.kareer.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sopt.kareer.domain.member.entity.enums.Country;
import org.sopt.kareer.domain.member.entity.enums.Degree;
import org.sopt.kareer.domain.member.entity.enums.LanguageLevel;
import org.sopt.kareer.domain.member.entity.enums.MemberStatus;
import org.sopt.kareer.domain.member.entity.enums.OAuthProvider;
import org.sopt.kareer.global.entity.BaseEntity;

import java.time.LocalDate;
import org.sopt.kareer.global.exception.customexception.BadRequestException;
import org.sopt.kareer.global.exception.errorcode.MemberErrorCode;

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
    @Column(length = 1000)
    private LanguageLevel languageLevel;

    @Enumerated(EnumType.STRING)
    private Degree degree;

    private String targetJobSkill;

    public void onboard(String name,
                        LocalDate birthDate,
                        Country country,
                        LanguageLevel languageLevel,
                        Degree degree,
                        LocalDate expectedGraduationDate,
                        String primaryMajor,
                        String secondaryMajor,
                        String targetJob,
                        String targetJobSkill) {
        assertPendingStatus();
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
                .build();
    }

    public void updateOAuthProfile(String name, String profileImageUrl) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public void assertOnboarded() {
        if (this.status == MemberStatus.PENDING) {
            throw new BadRequestException(MemberErrorCode.ONBOARDING_REQUIRED);
        }
    }

    private void assertPendingStatus() {
        if (this.status == MemberStatus.ACTIVE) {
            throw new BadRequestException(MemberErrorCode.ONBOARDING_ALREADY_COMPLETED);
        }
    }
}
