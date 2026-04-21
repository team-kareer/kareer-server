package org.sopt.kareer.domain.roadmap.entity.phaseaction;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phase_action_translations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"phase_actions_id", "language"}))
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhaseActionTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phase_actions_id", nullable = false)
    private PhaseAction phaseAction;

    @Column(nullable = false, length = 10)
    private String language;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String importance;

    public static PhaseActionTranslation create(PhaseAction phaseAction, String language,
                                                String title, String description, String importance) {
        return PhaseActionTranslation.builder()
                .phaseAction(phaseAction)
                .language(language)
                .title(title)
                .description(description)
                .importance(importance)
                .build();
    }
}
