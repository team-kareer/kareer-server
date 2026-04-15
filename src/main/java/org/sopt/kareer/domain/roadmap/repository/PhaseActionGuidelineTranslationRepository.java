package org.sopt.kareer.domain.roadmap.repository;

import org.sopt.kareer.domain.roadmap.entity.PhaseActionGuidelineTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhaseActionGuidelineTranslationRepository extends JpaRepository<PhaseActionGuidelineTranslation, Long> {

    List<PhaseActionGuidelineTranslation> findAllByGuideline_PhaseAction_IdIn(List<Long> phaseActionIds);

    @Query("""
        SELECT t.content
        FROM PhaseActionGuidelineTranslation t
        WHERE t.guideline.phaseAction.id = :phaseActionId
          AND t.language = :language
        ORDER BY t.guideline.id ASC
    """)
    List<String> findContentByPhaseActionIdAndLanguage(@Param("phaseActionId") Long phaseActionId,
                                                       @Param("language") String language);

    void deleteAllByGuideline_PhaseAction_IdInAndLanguage(List<Long> phaseActionIds, String language);

    void deleteAllByGuideline_PhaseAction_Phase_Roadmap_Member_Id(Long memberId);

}
