package org.sopt.kareer.domain.roadmap.entity.phase;

import jakarta.persistence.*;
import lombok.*;
import org.sopt.kareer.domain.roadmap.entity.Roadmap;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseStatus;
import org.sopt.kareer.global.entity.BaseEntity;

import java.time.LocalDate;

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
    @JoinColumn(name = "roadmap_id", nullable = false)
    private Roadmap roadmap;

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
    private Phase(Roadmap roadmap, int sequence, String goal, String description, PhaseStatus status, LocalDate startDate, LocalDate endDate) {
        this.roadmap = roadmap;
        this.sequence = sequence;
        this.goal = goal;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Phase create(Roadmap roadmap, int sequence, String goal, String description, PhaseStatus status, LocalDate startDate, LocalDate endDate) {
        return Phase.builder()
                .roadmap(roadmap)
                .sequence(sequence)
                .goal(goal)
                .description(description)
                .status(status)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
