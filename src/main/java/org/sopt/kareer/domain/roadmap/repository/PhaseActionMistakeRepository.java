package org.sopt.kareer.domain.roadmap.repository;

import org.sopt.kareer.domain.roadmap.entity.PhaseActionMistake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhaseActionMistakeRepository extends JpaRepository<PhaseActionMistake, Long> {

    @Query("""
        SELECT pam.content
        FROM PhaseActionMistake pam
        WHERE pam.phaseAction.id = :phaseActionId
        ORDER BY pam.id ASC
    """)
    List<String> findContentByPhaseActionId(@Param("phaseActionId") Long phaseActionId);
}
