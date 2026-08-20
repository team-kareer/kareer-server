package org.sopt.kareer.domain.roadmap.service.actionitem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.roadmap.entity.actionitem.ActionItem;
import org.sopt.kareer.domain.roadmap.entity.enums.ActionItemStatus;
import org.sopt.kareer.domain.roadmap.entity.enums.ActionItemType;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;
import org.sopt.kareer.domain.roadmap.repository.ActionItemRepository;
import org.sopt.kareer.domain.roadmap.repository.ActionItemTranslationRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ActionItemCommandServiceTest {

    @Mock
    private ActionItemRepository actionItemRepository;
    @Mock
    private ActionItemTranslationRepository actionItemTranslationRepository;

    private ActionItemCommandService actionItemCommandService;

    @BeforeEach
    void setUp() {
        actionItemCommandService = new ActionItemCommandService(
                actionItemRepository,
                actionItemTranslationRepository
        );
    }

    @Test
    void PhaseAction이_없는_사용자_Todo도_완료_상태를_토글한다() {
        Member member = Member.builder().id(1L).build();
        ActionItem actionItem = ActionItem.builder()
                .id(10L)
                .title("이력서 작성하기")
                .actionsType(ActionItemType.CAREER)
                .status(ActionItemStatus.ACTIVE)
                .deadline(LocalDate.now().plusDays(1))
                .completed(false)
                .member(member)
                .phaseAction(null)
                .build();
        given(actionItemRepository.findByIdAndMemberId(10L, 1L))
                .willReturn(Optional.of(actionItem));

        actionItemCommandService.toggleCompletion(1L, 10L);

        assertThat(actionItem.getCompleted()).isTrue();
    }

    @Test
    void 사용자_Todo를_ACTIVE_상태로_생성한다() {
        Member member = Member.builder().id(1L).build();
        LocalDate deadline = LocalDate.now().plusDays(1);
        given(actionItemRepository.save(any(ActionItem.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        ActionItem created = actionItemCommandService.createActionItem(
                member,
                ActionItemType.CAREER,
                "이력서 작성하기",
                deadline
        );

        assertThat(created.getTitle()).isEqualTo("이력서 작성하기");
        assertThat(created.getActionsType()).isEqualTo(ActionItemType.CAREER);
        assertThat(created.getDeadline()).isEqualTo(deadline);
        assertThat(created.getStatus()).isEqualTo(ActionItemStatus.ACTIVE);
        assertThat(created.getCompleted()).isFalse();
        assertThat(created.getMember()).isSameAs(member);
        assertThat(created.getPhaseAction()).isNull();
    }

    @Test
    void 제목과_마감일을_수정하고_기존_번역을_삭제한다() {
        ActionItem actionItem = existingActionItem();
        LocalDate changedDeadline = LocalDate.now().plusDays(10);
        given(actionItemRepository.findByIdAndMemberId(10L, 1L))
                .willReturn(Optional.of(actionItem));

        ActionItem updated = actionItemCommandService.updateActionItem(
                1L,
                10L,
                "수정된 제목",
                changedDeadline
        );

        assertThat(updated.getTitle()).isEqualTo("수정된 제목");
        assertThat(updated.getDeadline()).isEqualTo(changedDeadline);
        verify(actionItemTranslationRepository).deleteAllByActionItem_Id(10L);
    }

    @Test
    void 마감일만_수정하면_기존_번역을_유지한다() {
        ActionItem actionItem = existingActionItem();
        LocalDate changedDeadline = LocalDate.now().plusDays(10);
        given(actionItemRepository.findByIdAndMemberId(10L, 1L))
                .willReturn(Optional.of(actionItem));

        actionItemCommandService.updateActionItem(1L, 10L, null, changedDeadline);

        assertThat(actionItem.getTitle()).isEqualTo("기존 제목");
        assertThat(actionItem.getDeadline()).isEqualTo(changedDeadline);
        verify(actionItemTranslationRepository, never()).deleteAllByActionItem_Id(any());
    }

    @Test
    void 번역을_먼저_삭제한_후_ActionItem을_삭제한다() {
        ActionItem actionItem = existingActionItem();
        given(actionItemRepository.findByIdAndMemberId(10L, 1L))
                .willReturn(Optional.of(actionItem));

        actionItemCommandService.deleteActionItem(1L, 10L);

        var order = inOrder(actionItemTranslationRepository, actionItemRepository);
        order.verify(actionItemTranslationRepository).deleteAllByActionItem_Id(10L);
        order.verify(actionItemRepository).delete(actionItem);
    }

    @Test
    void 다른_사용자의_ActionItem은_수정할_수_없다() {
        given(actionItemRepository.findByIdAndMemberId(10L, 2L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> actionItemCommandService.updateActionItem(
                2L,
                10L,
                "수정 시도",
                null
        )).isInstanceOf(RoadMapException.class);

        verify(actionItemTranslationRepository, never()).deleteAllByActionItem_Id(any());
    }

    private ActionItem existingActionItem() {
        Member member = Member.builder().id(1L).build();
        return ActionItem.builder()
                .id(10L)
                .title("기존 제목")
                .actionsType(ActionItemType.VISA)
                .status(ActionItemStatus.ACTIVE)
                .deadline(LocalDate.now().plusDays(1))
                .completed(false)
                .member(member)
                .build();
    }
}
