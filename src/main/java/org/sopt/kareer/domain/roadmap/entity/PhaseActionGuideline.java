package org.sopt.kareer.domain.roadmap.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "phase_action_guidelines")
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PhaseActionGuideline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phase_actions_id")
    private PhaseAction phaseAction;

    @Builder
    private PhaseActionGuideline(String content, PhaseAction phaseAction) {
        this.content = content;
        this.phaseAction = phaseAction;
    }

    public static PhaseActionGuideline create(String content, PhaseAction phaseAction) {
        return PhaseActionGuideline.builder()
                .content(content)
                .phaseAction(phaseAction)
                .build();
    }
}
