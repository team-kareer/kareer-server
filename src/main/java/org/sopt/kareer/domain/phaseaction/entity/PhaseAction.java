package org.sopt.kareer.domain.phaseaction.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.kareer.domain.phase.entity.Phase;
import org.sopt.kareer.domain.phaseaction.entity.enums.PhaseActionType;
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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "type", nullable = false)
    private PhaseActionType type;

    @Column(nullable = false)
    private LocalDate deadline;

    @Column(nullable = false)
    private String importance;

    @Column(nullable = false)
    private String guideline;

    @Column(name = "common_mistakes", nullable = false)
    private String commonMistakes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phase_id", nullable = false)
    private Phase phase;
}
