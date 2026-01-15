package org.sopt.kareer.domain.roadmap.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "phase_action_mistakes")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PhaseActionMistake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phase_action_mistakes_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phase_actions_id")
    private PhaseAction phaseAction;

    @Builder
    private PhaseActionMistake(String content, PhaseAction phaseAction) {
        this.content = content;
        this.phaseAction = phaseAction;
    }

    public static PhaseActionMistake create(String content, PhaseAction phaseAction) {
        return PhaseActionMistake.builder()
                .content(content)
                .phaseAction(phaseAction)
                .build();
    }
}
