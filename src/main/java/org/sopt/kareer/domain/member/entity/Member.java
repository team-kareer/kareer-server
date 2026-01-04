package org.sopt.kareer.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.kareer.global.entity.BaseEntity;

import java.time.LocalDate;

@Table(name = "members")
@Entity
@Getter
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
    private LocalDate birthDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Country country;

    @Column(nullable = false)
    private String primaryMajor;

    private String secondaryMajor;

    @Column(nullable = false)
    private String targetJob;

    @Column(nullable = false)
    private LocalDate graduationDate;

    private LocalDate expectedGraduationDate;

    private String personalBackground;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisaType visaType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisaStatus visaStatus;

    private LocalDate visaExpiredAt;

    private String university;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LanguageLevel languageLevel;

}
