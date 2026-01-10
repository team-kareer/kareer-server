package org.sopt.kareer.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sopt.kareer.global.entity.BaseEntity;

import java.time.LocalDate;

@Table(name = "members")
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

    @Column(nullable = false, unique = true)
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

    private String university;

    @Enumerated(EnumType.STRING)
    private LanguageLevel languageLevel;

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

}
