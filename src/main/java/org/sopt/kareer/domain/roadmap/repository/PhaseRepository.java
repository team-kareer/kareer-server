package org.sopt.kareer.domain.roadmap.repository;

import org.sopt.kareer.domain.roadmap.entity.Phase;
import org.sopt.kareer.domain.roadmap.entity.enums.RoadmapActiveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhaseRepository extends JpaRepository<Phase, Long>, PhaseRepositoryCustom {

    boolean existsByIdAndRoadmap_Member_Id(Long phaseId, Long memberId);

    boolean existsByIdAndRoadmap_Member_IdAndRoadmap_Status(Long phaseId, Long memberId, RoadmapActiveStatus status);

    void deleteAllByRoadmap_Member_Id(Long memberId);
}
