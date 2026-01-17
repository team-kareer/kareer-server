package org.sopt.kareer.domain.roadmap.repository;

import org.sopt.kareer.domain.roadmap.entity.PhaseActionGuideline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhaseActionGuidelineRepository extends JpaRepository<PhaseActionGuideline, Long> {

    @Query("""
        SELECT pag.content
        FROM PhaseActionGuideline pag
        WHERE pag.phaseAction.id = :phaseActionId
        ORDER BY pag.id ASC
    """)
    List<String> findContentByPhaseActionId(@Param("phaseActionId") Long phaseActionId);
}
