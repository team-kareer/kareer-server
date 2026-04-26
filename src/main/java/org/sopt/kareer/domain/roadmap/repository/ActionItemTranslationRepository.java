package org.sopt.kareer.domain.roadmap.repository;

import org.sopt.kareer.domain.roadmap.entity.actionitem.ActionItemTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActionItemTranslationRepository extends JpaRepository<ActionItemTranslation, Long> {

    List<ActionItemTranslation> findAllByActionItem_IdInAndLanguage(List<Long> actionItemIds, String language);

    void deleteAllByActionItem_IdInAndLanguage(List<Long> actionItemIds, String language);

    void deleteAllByActionItem_Member_Id(Long memberId);
}
