package org.sopt.kareer.domain.member.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.kareer.domain.member.entity.LocalizedOnboardCategory;
import org.sopt.kareer.domain.member.entity.enums.LocalizedOnboardCategoryType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalizedOnboardCategoryRepository extends JpaRepository<LocalizedOnboardCategory, Long> {

    @EntityGraph(attributePaths = "translations")
    List<LocalizedOnboardCategory> findAllByTypeOrderByUseOrderAscIdAsc(LocalizedOnboardCategoryType type);

    @EntityGraph(attributePaths = "translations")
    Optional<LocalizedOnboardCategory> findByTypeAndCode(LocalizedOnboardCategoryType type, String code);
}
