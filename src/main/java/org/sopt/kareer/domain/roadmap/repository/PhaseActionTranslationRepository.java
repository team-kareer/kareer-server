package org.sopt.kareer.domain.roadmap.repository;

import org.sopt.kareer.domain.roadmap.entity.PhaseActionTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhaseActionTranslationRepository extends JpaRepository<PhaseActionTranslation, Long> {

    List<PhaseActionTranslation> findAllByPhaseAction_IdInAndLanguage(List<Long> phaseActionIds, String language);

    Optional<PhaseActionTranslation> findByPhaseAction_IdAndLanguage(Long phaseActionId, String language);

    void deleteAllByPhaseAction_IdInAndLanguage(List<Long> phaseActionIds, String language);

    void deleteAllByPhaseAction_Phase_Roadmap_Member_Id(Long memberId);
}
