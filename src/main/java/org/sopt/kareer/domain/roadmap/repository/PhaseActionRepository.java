package org.sopt.kareer.domain.roadmap.repository;

import org.sopt.kareer.domain.roadmap.entity.PhaseAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhaseActionRepository extends JpaRepository<PhaseAction, Long> {

    List<PhaseAction> findByPhaseIdAndCompletedIsFalse(Long phaseId);
}
