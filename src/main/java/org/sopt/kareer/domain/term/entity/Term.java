package org.sopt.kareer.domain.term.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sopt.kareer.domain.term.entity.enums.TermType;
import org.sopt.kareer.global.entity.BaseEntity;

@Table(name = "terms")
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Term extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "term_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TermType type;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private boolean required;

    @Column(nullable = false)
    private boolean active;
}
