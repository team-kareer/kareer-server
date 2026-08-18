package org.sopt.kareer.domain.roadmap.service.actionitem;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.entity.actionitem.ActionItem;
import org.sopt.kareer.domain.roadmap.entity.actionitem.ActionItemTranslation;
import org.sopt.kareer.domain.roadmap.entity.enums.ActionItemStatus;
import org.sopt.kareer.domain.roadmap.repository.ActionItemRepository;
import org.sopt.kareer.domain.roadmap.repository.ActionItemTranslationRepository;
import org.sopt.kareer.domain.roadmap.service.dto.response.ActionItemDetail;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActionItemQueryService {

    private final ActionItemRepository actionItemRepository;
    private final ActionItemTranslationRepository actionItemTranslationRepository;

    public List<ActionItemDetail> getAllActiveItems(Long memberId) {
        List<ActionItem> activeItems = actionItemRepository
                .findAllByMemberIdAndStatus(memberId, ActionItemStatus.ACTIVE);

        String language = LocaleContextHolder.getLocale().toLanguageTag();
        List<Long> itemIds = activeItems.stream().map(ActionItem::getId).toList();
        Map<Long, ActionItemTranslation> translationMap = actionItemTranslationRepository
                .findAllByActionItem_IdInAndLanguage(itemIds, language)
                .stream()
                .collect(Collectors.toMap(t -> t.getActionItem().getId(), t -> t));

        return activeItems.stream()
                .map(item -> {
                    ActionItemTranslation t = translationMap.get(item.getId());
                    String title = (t != null && t.getTitle() != null) ? t.getTitle() : item.getTitle();
                    return new ActionItemDetail(
                            item.getId(), item.getActionsType(), title,
                            item.getDeadline(), Boolean.TRUE.equals(item.getCompleted()));
                })
                .toList();
    }
}
