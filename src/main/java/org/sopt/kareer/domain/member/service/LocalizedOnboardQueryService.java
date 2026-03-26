package org.sopt.kareer.domain.member.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.dto.response.LocalizedItemResponse;
import org.sopt.kareer.domain.member.dto.response.OnboardCountriesResponse;
import org.sopt.kareer.domain.member.dto.response.OnboardFieldsResponse;
import org.sopt.kareer.domain.member.dto.response.OnboardMajorsResponse;
import org.sopt.kareer.domain.member.dto.response.OnboardUniversitiesResponse;
import org.sopt.kareer.domain.member.entity.*;
import org.sopt.kareer.domain.member.entity.enums.LocalizedOnboardCategoryType;
import org.sopt.kareer.domain.member.repository.LocalizedOnboardCategoryRepository;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocalizedOnboardQueryService {

    private static final String DEFAULT_LANGUAGE = "en";
    private static final String EMPTY_LABEL = "";

    private final LocalizedOnboardCategoryRepository categoryRepository;


    public OnboardFieldsResponse getFields() {
        return OnboardFieldsResponse.of(toItemResponses(fetchByType(LocalizedOnboardCategoryType.FIELD)));
    }

    public OnboardMajorsResponse getMajors() {
        return OnboardMajorsResponse.of(toItemResponses(fetchByType(LocalizedOnboardCategoryType.MAJOR)));
    }

    public OnboardUniversitiesResponse getUniversities() {
        return OnboardUniversitiesResponse.of(toItemResponses(fetchByType(LocalizedOnboardCategoryType.UNIVERSITY)));
    }

    public OnboardCountriesResponse getCountries() {
        return OnboardCountriesResponse.of(toItemResponses(fetchByType(LocalizedOnboardCategoryType.COUNTRY)));
    }

    public String resolveLabelByCode(LocalizedOnboardCategoryType type, String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        Locale locale = LocaleContextHolder.getLocale();
        String languageTag = locale != null ? locale.toLanguageTag() : null;
        String language = locale != null ? locale.getLanguage() : null;

        return categoryRepository.findByTypeAndCode(type, code)
                .map(category -> resolveLabel(category, languageTag, language))
                .orElse(code);
    }

    private List<LocalizedOnboardCategory> fetchByType(LocalizedOnboardCategoryType type) {
        return categoryRepository.findAllByTypeOrderByUseOrderAscIdAsc(type);
    }

    private List<LocalizedItemResponse> toItemResponses(List<LocalizedOnboardCategory> categories) {
        Locale locale = LocaleContextHolder.getLocale();
        String languageTag = locale != null ? locale.toLanguageTag() : null;
        String language = locale != null ? locale.getLanguage() : null;

        return categories.stream()
                .map(category -> new LocalizedItemResponse(
                        category.getCode(),
                        resolveLabel(category, languageTag, language)))
                .toList();
    }

    private String resolveLabel(LocalizedOnboardCategory category, String preferredTag, String preferredLanguage) {
        return findLabel(category, preferredTag)
                .or(() -> findLabel(category, preferredLanguage))
                .or(() -> findLabel(category, DEFAULT_LANGUAGE))
                .or(() -> category.getTranslations().stream().findFirst().map(
                        LocalizedOnboardCategoryTranslation::getLabel))
                .orElse(EMPTY_LABEL);
    }

    private Optional<String> findLabel(LocalizedOnboardCategory category, String language) {
        if (language == null || language.isBlank()) {
            return Optional.empty();
        }
        return category.getTranslations().stream()
                .filter(t -> t.getLanguage().equalsIgnoreCase(language))
                .map(LocalizedOnboardCategoryTranslation::getLabel)
                .findFirst();
    }
}
