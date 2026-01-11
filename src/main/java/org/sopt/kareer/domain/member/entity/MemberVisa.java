package org.sopt.kareer.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import org.sopt.kareer.global.entity.BaseEntity;

@Table(name = "member_visas")
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberVisa extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_visa_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisaType visaType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisaStatus visaStatus;

    @Column(nullable = false)
    private LocalDate visaExpiredAt;

    private Integer visaPoint;

    @Column(nullable = false)
    private LocalDate visaStartDate;
}
