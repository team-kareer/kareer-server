package org.sopt.kareer.domain.roadmap.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseStatus;
import org.sopt.kareer.global.entity.BaseEntity;

@Entity
@Table(name = "phases")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Phase extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phase_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private int sequence;

    @Column(nullable = false)
    private String goal;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhaseStatus status;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Builder
    private Phase(Member member, int sequence, String goal, String description, PhaseStatus status, LocalDate startDate, LocalDate endDate) {
        this.member = member;
        this.sequence = sequence;
        this.goal = goal;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Phase create(Member member, int sequence, String goal, String description, PhaseStatus status, LocalDate startDate, LocalDate endDate) {
        return Phase.builder()
                .member(member)
                .sequence(sequence)
                .goal(goal)
                .description(description)
                .status(status)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
