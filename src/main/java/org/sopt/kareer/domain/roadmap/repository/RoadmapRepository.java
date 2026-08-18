package org.sopt.kareer.domain.roadmap.repository;

import org.sopt.kareer.domain.roadmap.entity.Roadmap;
import org.sopt.kareer.domain.roadmap.entity.enums.RoadmapActiveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {

    Optional<Roadmap> findByMember_IdAndStatus(Long memberId, RoadmapActiveStatus status);

    List<Roadmap> findAllByMember_Id(Long memberId);

    void deleteAllByMember_Id(Long memberId);
}
