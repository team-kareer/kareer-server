package org.sopt.kareer.domain.roadmap.repository;

import org.sopt.kareer.domain.roadmap.entity.ActionItem;
import org.sopt.kareer.domain.roadmap.entity.enums.ActionItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ActionItemRepository extends JpaRepository<ActionItem, Long> {

    boolean existsByPhaseActionIdAndCompletedFalse(Long phaseActionId);

    List<ActionItem> findAllByPhaseActionId(Long phaseActionId);

    List<ActionItem> findAllByMemberIdAndStatus(Long memberId, ActionItemStatus status);

    List<ActionItem> findAllByMemberIdAndCompletedTrue(Long memberId);

    Optional<ActionItem> findByIdAndMember_Id(@Param("actionItemId") Long actionItemId, @Param("memberId") Long memberId);
}
