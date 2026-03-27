package org.sopt.kareer.domain.roadmap.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "action_item_translations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"action_item_id", "language"}))
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActionItemTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_item_id", nullable = false)
    private ActionItem actionItem;

    @Column(nullable = false, length = 10)
    private String language;

    private String title;

    public static ActionItemTranslation create(ActionItem actionItem, String language, String title) {
        return ActionItemTranslation.builder()
                .actionItem(actionItem)
                .language(language)
                .title(title)
                .build();
    }
}
