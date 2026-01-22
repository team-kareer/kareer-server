package org.sopt.kareer.domain.roadmap.repository;

import org.sopt.kareer.domain.roadmap.entity.PhaseAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PhaseActionRepository extends JpaRepository<PhaseAction, Long> {

    List<PhaseAction> findByPhaseIdAndCompletedIsFalse(Long phaseId);

    @Query("""
        SELECT pa
        FROM PhaseAction pa
        JOIN FETCH pa.phase p
        JOIN FETCH p.member m
        WHERE pa.id = :phaseActionId
                AND m.id = :memberId
        """)
    Optional<PhaseAction> findByIdAndMemberId(@Param("phaseActionId") Long phaseActionId, @Param("memberId") Long memberId);
}
