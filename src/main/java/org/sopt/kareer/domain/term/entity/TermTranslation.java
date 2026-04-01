package org.sopt.kareer.domain.term.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sopt.kareer.global.entity.BaseEntity;

@Entity
@Table(name = "term_translations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_term_translation_language",
                        columnNames = {"term_id", "language_code"})
        })
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TermTranslation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "term_translation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;

    @Column(name = "language_code", nullable = false, length = 10)
    private String languageCode;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}
