package org.sopt.kareer.domain.term.repository;

import org.sopt.kareer.domain.term.entity.TermTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TermTranslationRepository extends JpaRepository<TermTranslation, Long> {

    List<TermTranslation> findAllByTerm_IdInAndLanguageCode(List<Long> termIds, String languageCode);

}
