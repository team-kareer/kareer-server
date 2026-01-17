package org.sopt.kareer.domain.roadmap.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseActionType;
import org.sopt.kareer.global.entity.BaseEntity;

@Entity
@Table(name = "phase_actions")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhaseAction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phase_actions_id")
    private Long id;

    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PhaseActionType type;

    @Column(nullable = false)
    private LocalDate deadline;

    @Column(nullable = false)
    private String importance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phase_id", nullable = false)
    private Phase phase;

    @Default
    @Column(nullable = false)
    private Boolean added = false;

    @Default
    @Column(nullable = false)
    private Boolean completed = false;

    @Default
    @Column(nullable = false)
    private boolean isCompleted = false;


    @Builder
    private PhaseAction(String title, String description, PhaseActionType type, LocalDate deadline, String importance, Phase phase) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.deadline = deadline;
        this.importance = importance;
        this.phase = phase;
    }

    public static PhaseAction create(String title,  String description, PhaseActionType type, LocalDate deadline, String importance, Phase phase) {
        return PhaseAction.builder()
                .title(title)
                .description(description)
                .type(type)
                .added(false)
                .completed(false)
                .deadline(deadline)
                .importance(importance)
                .phase(phase)
                .build();
    }

    public void markCompleted() {
        this.completed = true;
    }

    public void markAdded() {
        this.added = true;
    }
}
