package org.sopt.kareer.domain.member.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;
import org.sopt.kareer.domain.member.entity.enums.*;
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

    @Column(nullable = false)
    private LocalDate visaStartDate;

    public static MemberVisa createMemberVisa(
            Member member,
            VisaType visaType,
            LocalDate visaExpiredAt,
            LocalDate visaStartDate
    ) {
        return MemberVisa.builder()
                .member(member)
                .visaType(visaType)
                .visaStatus(VisaStatus.ACTIVE)
                .visaExpiredAt(visaExpiredAt)
                .visaStartDate(visaStartDate)
                .build();
    }

    public void updateVisa(
            VisaType visaType,
            LocalDate visaExpiredAt
    ){
        this.visaType = visaType;
        this.visaExpiredAt = visaExpiredAt;
    }

}
