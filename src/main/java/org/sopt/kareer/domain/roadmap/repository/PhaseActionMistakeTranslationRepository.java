package org.sopt.kareer.domain.roadmap.repository;

import org.sopt.kareer.domain.roadmap.entity.PhaseActionMistakeTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhaseActionMistakeTranslationRepository extends JpaRepository<PhaseActionMistakeTranslation, Long> {

    List<PhaseActionMistakeTranslation> findAllByMistake_PhaseAction_IdIn(List<Long> phaseActionIds);

    @Query("""
        SELECT t.content
        FROM PhaseActionMistakeTranslation t
        WHERE t.mistake.phaseAction.id = :phaseActionId
          AND t.language = :language
        ORDER BY t.mistake.id ASC
    """)
    List<String> findContentByPhaseActionIdAndLanguage(@Param("phaseActionId") Long phaseActionId,
                                                       @Param("language") String language);

    void deleteAllByMistake_PhaseAction_IdInAndLanguage(List<Long> phaseActionIds, String language);

}
