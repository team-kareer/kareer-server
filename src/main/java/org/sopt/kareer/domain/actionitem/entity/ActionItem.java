package org.sopt.kareer.domain.actionitem.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.kareer.domain.actionitem.entity.enums.ActionItemType;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.phaseaction.entity.PhaseAction;
import org.sopt.kareer.global.entity.BaseEntity;

@Entity
@Table(name = "action_items")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActionItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_item_id")
    private Long id;

    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionItemType actionsType;

    @Column(nullable = false)
    private LocalDate deadline;

    @Default
    @Column(nullable = false)
    private boolean completed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phase_actions_id", nullable = false)
    private PhaseAction phaseAction;

    @Builder
    private ActionItem(String title, ActionItemType actionsType, LocalDate deadline, boolean completed, Member member, PhaseAction phaseAction) {
        this.title = title;
        this.actionsType = actionsType;
        this.deadline = deadline;
        this.completed = completed;
        this.member = member;
        this.phaseAction = phaseAction;
    }

    public static ActionItem create(String title, ActionItemType actionsType, LocalDate deadline, Member member, PhaseAction phaseAction) {
        return ActionItem.builder()
                .title(title)
                .actionsType(actionsType)
                .deadline(deadline)
                .completed(false)
                .member(member)
                .phaseAction(phaseAction)
                .build();
    }
}
