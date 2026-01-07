package org.sopt.kareer.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sopt.kareer.global.entity.BaseEntity;

import java.time.LocalDate;

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

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisaType visaType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisaStatus visaStatus;

    private LocalDate visaExpiredAt;


}
