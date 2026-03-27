package org.sopt.kareer.domain.roadmap.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phase_action_guideline_translations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"guideline_id", "language"}))
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhaseActionGuidelineTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guideline_id", nullable = false)
    private PhaseActionGuideline guideline;

    @Column(nullable = false, length = 10)
    private String language;

    @Column(columnDefinition = "TEXT")
    private String content;

    public static PhaseActionGuidelineTranslation create(PhaseActionGuideline guideline, String language, String content) {
        return PhaseActionGuidelineTranslation.builder()
                .guideline(guideline)
                .language(language)
                .content(content)
                .build();
    }
}
