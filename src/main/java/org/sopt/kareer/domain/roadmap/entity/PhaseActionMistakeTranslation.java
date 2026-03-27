package org.sopt.kareer.domain.roadmap.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phase_action_mistake_translations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"mistake_id", "language"}))
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhaseActionMistakeTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mistake_id", nullable = false)
    private PhaseActionMistake mistake;

    @Column(nullable = false, length = 10)
    private String language;

    @Column(columnDefinition = "TEXT")
    private String content;

    public static PhaseActionMistakeTranslation create(PhaseActionMistake mistake, String language, String content) {
        return PhaseActionMistakeTranslation.builder()
                .mistake(mistake)
                .language(language)
                .content(content)
                .build();
    }
}
