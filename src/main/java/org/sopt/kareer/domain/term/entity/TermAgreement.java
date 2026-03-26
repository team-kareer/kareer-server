package org.sopt.kareer.domain.term.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.global.entity.BaseEntity;

@Table(name = "term_agreeements")
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TermAgreement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "term_agreement_id")
    private Long id;

    @Column(nullable = false)
    private boolean agreed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;
}
