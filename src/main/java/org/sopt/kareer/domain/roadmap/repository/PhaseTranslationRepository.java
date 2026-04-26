package org.sopt.kareer.domain.roadmap.repository;

import org.sopt.kareer.domain.roadmap.entity.phase.PhaseTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhaseTranslationRepository extends JpaRepository<PhaseTranslation, Long> {

    List<PhaseTranslation> findAllByPhase_IdInAndLanguage(List<Long> phaseIds, String language);

    void deleteAllByPhase_IdInAndLanguage(List<Long> phaseIds, String language);

    void deleteAllByPhase_Roadmap_Member_Id(Long memberId);
}
