package org.sopt.kareer.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sopt.kareer.domain.term.entity.Term;
import org.sopt.kareer.global.entity.BaseEntity;

@Table(name = "member_terms")
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberTerm extends BaseEntity {

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

    public MemberTerm(boolean agreed, Member member, Term term) {
        this.agreed = agreed;
        this.member = member;
        this.term = term;
    }

    public static MemberTerm create(boolean agreed, Member member, Term term) {
        return new MemberTerm(agreed, member, term);
    }
}
