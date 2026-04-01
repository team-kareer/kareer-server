package org.sopt.kareer.domain.roadmap.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phase_translations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"phase_id", "language"}))
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhaseTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phase_id", nullable = false)
    private Phase phase;

    @Column(nullable = false, length = 10)
    private String language;

    @Column(nullable = false)
    private String goal;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    public static PhaseTranslation create(Phase phase, String language, String goal, String description) {
        return PhaseTranslation.builder()
                .phase(phase)
                .language(language)
                .goal(goal)
                .description(description)
                .build();
    }
}
