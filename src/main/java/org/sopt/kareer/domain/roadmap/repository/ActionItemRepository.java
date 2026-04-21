package org.sopt.kareer.domain.roadmap.repository;

import org.sopt.kareer.domain.roadmap.entity.actionitem.ActionItem;
import org.sopt.kareer.domain.roadmap.entity.enums.ActionItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ActionItemRepository extends JpaRepository<ActionItem, Long> {

    boolean existsByPhaseActionIdAndCompletedFalse(Long phaseActionId);

    List<ActionItem> findAllByPhaseActionId(Long phaseActionId);

    List<ActionItem> findAllByMemberIdAndStatus(Long memberId, ActionItemStatus status);

    List<ActionItem> findAllByMemberIdAndCompletedTrue(Long memberId);

    Optional<ActionItem> findByIdAndMemberId(@Param("actionItemId") Long actionItemId, @Param("memberId") Long memberId);

    void deleteAllByMemberId(Long memberId);

    @Modifying
    @Query("""
        UPDATE ActionItem ai
        SET ai.status = 'INACTIVE'
        WHERE ai.phaseAction.id IN (
            SELECT pa.id FROM PhaseAction pa
            WHERE pa.phase.id IN (
                SELECT p.id FROM Phase p WHERE p.roadmap.id = :roadmapId
            )
        )
    """)
    void deactivateAllByRoadmapId(@Param("roadmapId") Long roadmapId);
}
