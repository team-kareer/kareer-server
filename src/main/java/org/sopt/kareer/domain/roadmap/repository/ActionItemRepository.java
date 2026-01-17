package org.sopt.kareer.domain.roadmap.repository;

import java.util.List;
import org.sopt.kareer.domain.roadmap.entity.ActionItem;
import org.sopt.kareer.domain.roadmap.entity.enums.ActionItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionItemRepository extends JpaRepository<ActionItem, Long> {

    boolean existsByPhaseActionIdAndCompletedFalse(Long phaseActionId);

    List<ActionItem> findAllByPhaseActionId(Long phaseActionId);

    List<ActionItem> findAllByMemberIdAndStatus(Long memberId, ActionItemStatus status);
}
