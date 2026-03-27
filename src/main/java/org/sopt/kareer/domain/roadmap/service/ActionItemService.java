package org.sopt.kareer.domain.roadmap.service;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.sopt.kareer.domain.roadmap.dto.response.ActionItemListResponse;
import org.sopt.kareer.domain.roadmap.dto.response.ActionItemResponse;
import org.sopt.kareer.domain.roadmap.entity.ActionItem;
import org.sopt.kareer.domain.roadmap.entity.ActionItemTranslation;
import org.sopt.kareer.domain.roadmap.entity.PhaseAction;
import org.sopt.kareer.domain.roadmap.entity.enums.ActionItemStatus;
import org.sopt.kareer.domain.roadmap.entity.enums.ActionItemType;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;
import org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode;
import org.sopt.kareer.domain.roadmap.repository.ActionItemRepository;
import org.sopt.kareer.domain.roadmap.repository.ActionItemTranslationRepository;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActionItemService {

    private final ActionItemRepository actionItemRepository;
    private final ActionItemTranslationRepository actionItemTranslationRepository;

    @Transactional
    public void toggleCompletion(Long memberId, Long actionItemId) {
        ActionItem actionItem = actionItemRepository.findByIdAndMemberId(actionItemId, memberId)
                .orElseThrow(() -> new RoadMapException(RoadmapErrorCode.ACTION_ITEM_NOT_FOUND));

        if (actionItem.getStatus() == ActionItemStatus.INACTIVE) {
            throw new RoadMapException(RoadmapErrorCode.ACTION_ITEM_INACTIVE);
        }

        PhaseAction phaseAction = actionItem.getPhaseAction();

        if (phaseAction.getCompleted()) {
            throw new RoadMapException(RoadmapErrorCode.PHASE_ACTION_ALREADY_COMPLETED);
        }

        actionItem.toggleCompletion();

        if (actionItem.getCompleted()) {
            boolean hasIncompleteItems = actionItemRepository.existsByPhaseActionIdAndCompletedFalse(
                    phaseAction.getId());
            if (!hasIncompleteItems) {
                phaseAction.markCompleted();
                List<ActionItem> phaseActionItems = actionItemRepository.findAllByPhaseActionId(phaseAction.getId());
                phaseActionItems.forEach(ActionItem::deactivate);
            }
        }
    }

    public ActionItemListResponse getAllActionItems(Long memberId) {
        List<ActionItem> activeActionItems = actionItemRepository
                .findAllByMemberIdAndStatus(memberId, ActionItemStatus.ACTIVE);

        String language = LocaleContextHolder.getLocale().toLanguageTag();
        List<Long> itemIds = activeActionItems.stream().map(ActionItem::getId).toList();
        Map<Long, ActionItemTranslation> translationMap = actionItemTranslationRepository
                .findAllByActionItem_IdInAndLanguage(itemIds, language)
                .stream()
                .collect(Collectors.toMap(t -> t.getActionItem().getId(), t -> t));

        final Map<Long, ActionItemTranslation> finalTranslationMap = translationMap;

        List<ActionItemResponse> visaActionItems = activeActionItems.stream()
                .filter(item -> item.getActionsType() == ActionItemType.VISA)
                .map(item -> toResponse(item, finalTranslationMap))
                .toList();

        List<ActionItemResponse> careerActionItems = activeActionItems.stream()
                .filter(item -> item.getActionsType() == ActionItemType.CAREER)
                .map(item -> toResponse(item, finalTranslationMap))
                .toList();

        return new ActionItemListResponse(visaActionItems, careerActionItems);
    }

    private ActionItemResponse toResponse(ActionItem item, Map<Long, ActionItemTranslation> translationMap) {
        ActionItemTranslation t = translationMap.get(item.getId());
        String title = (t != null && t.getTitle() != null) ? t.getTitle() : item.getTitle();
        return new ActionItemResponse(item.getId(), title, item.getDeadline(),
                Boolean.TRUE.equals(item.getCompleted()));
    }
}
