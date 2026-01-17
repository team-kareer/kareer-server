package org.sopt.kareer.domain.member.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;
import org.sopt.kareer.domain.member.entity.enums.VisaStatus;
import org.sopt.kareer.domain.member.entity.enums.VisaType;
import org.sopt.kareer.domain.member.exception.MemberException;
import org.sopt.kareer.global.entity.BaseEntity;
import org.sopt.kareer.domain.member.exception.MemberErrorCode;

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

    public static MemberVisa createMemberVisa(
            Member member,
            VisaType visaType,
            LocalDate visaExpiredAt,
            Integer visaPoint,
            LocalDate visaStartDate
    ) {
        return MemberVisa.builder()
                .member(member)
                .visaType(visaType)
                .visaStatus(VisaStatus.ACTIVE)
                .visaExpiredAt(visaExpiredAt)
                .visaPoint(visaPoint)
                .visaStartDate(visaStartDate)
                .build();
    }

    @PrePersist
    @PreUpdate
    private void validateVisaPoint() {
        if (visaType == null) {
            return;
        }

        if (visaType == VisaType.D10) {
            if (visaPoint == null) {
                throw new MemberException(MemberErrorCode.INVALID_VISA_POINT);
            }
            return;
        }

        if (visaPoint != null) {
            throw new MemberException(MemberErrorCode.INVALID_VISA_POINT);
        }
    }
}
