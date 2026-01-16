package org.sopt.kareer.domain.roadmap.repository;

import org.sopt.kareer.domain.roadmap.dto.response.HomePhaseActionResponse;
import org.sopt.kareer.domain.roadmap.entity.Phase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhaseRepository extends JpaRepository<Phase, Long>, PhaseRepositoryCustom {

    @Query("""
        SELECT new org.sopt.kareer.domain.roadmap.dto.response.HomePhaseActionResponse(
            pa.type,
            pa.title,
            pa.deadline
        )
        FROM PhaseAction pa
        WHERE pa.phase.id = :phaseId
            AND pa.isCompleted = false
    """)
    List<HomePhaseActionResponse> getHomePhaseDetail(@Param("phaseId") Long phaseId);
}
