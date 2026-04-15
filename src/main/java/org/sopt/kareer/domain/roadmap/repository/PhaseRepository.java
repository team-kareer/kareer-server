package org.sopt.kareer.domain.roadmap.repository;

import org.sopt.kareer.domain.roadmap.entity.Phase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhaseRepository extends JpaRepository<Phase, Long>, PhaseRepositoryCustom {

    boolean existsByIdAndRoadmap_Member_Id(Long phaseId, Long memberId);

    void deleteAllByRoadmap_Member_Id(Long memberId);
}
