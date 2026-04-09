package org.sopt.kareer.domain.term.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.term.dto.response.TermsResponse;
import org.sopt.kareer.domain.term.entity.Term;
import org.sopt.kareer.domain.term.entity.TermTranslation;
import org.sopt.kareer.domain.term.repository.TermRepository;
import org.sopt.kareer.domain.term.repository.TermTranslationRepository;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermService {

    private static final String DEFAULT_LANGUAGE = "ko";

    private final TermRepository termRepository;
    private final TermTranslationRepository termTranslationRepository;

    public TermsResponse getTerms() {
        List<String> languagePreferences = resolveLanguagePreferences();
        List<Term> terms = termRepository.findByActiveTrue();
        if (terms.isEmpty()) {
            return TermsResponse.from(List.of());
        }

        List<Long> termIds = terms.stream()
                .map(Term::getId)
                .toList();

        Map<String, Map<Long, TermTranslation>> translationsByLanguage = loadTranslationsByLanguage(termIds, languagePreferences);

        List<TermsResponse.TermResponse> termResponses = terms.stream()
                .sorted(Comparator.comparing(term -> term.getType().getOrder()))
                .map(term -> {
                    TermTranslation translation = findTranslation(term.getId(), translationsByLanguage, languagePreferences);

                    String title = translation != null ? translation.getTitle() : "";
                    String content = translation != null ? translation.getContent() : "";
                    return TermsResponse.TermResponse.of(term, title, content);
                })
                .toList();

        return TermsResponse.from(termResponses);
    }

    public List<Term> getActiveTerms() {
        return termRepository.findByActiveTrue();
    }

    private static List<String> resolveLanguagePreferences() {
        Locale locale = LocaleContextHolder.getLocale();
        List<String> preferences = new ArrayList<>();

        addLanguage(preferences, locale != null ? locale.toLanguageTag() : null);
        addLanguage(preferences, locale != null ? locale.getLanguage() : null);
        addLanguage(preferences, DEFAULT_LANGUAGE);

        return List.copyOf(preferences);
    }

    private static void addLanguage(List<String> target, String language) {
        if (language == null || language.isBlank()) {
            return;
        }
        String normalized = language.toLowerCase(Locale.ROOT);
        if (!target.contains(normalized)) {
            target.add(normalized);
        }
    }

    private Map<String, Map<Long, TermTranslation>> loadTranslationsByLanguage(List<Long> termIds,
                                                                               List<String> languagePreferences) {
        Map<String, Map<Long, TermTranslation>> translationsByLanguage = new LinkedHashMap<>();
        for (String language : languagePreferences) {
            List<TermTranslation> translations = termTranslationRepository
                    .findAllByTerm_IdInAndLanguageCode(termIds, language);
            translationsByLanguage.put(language, groupByTermId(translations));
        }
        return translationsByLanguage;
    }

    private static TermTranslation findTranslation(Long termId,
                                                    Map<String, Map<Long, TermTranslation>> translationsByLanguage,
                                                    List<String> languagePreferences) {
        for (String language : languagePreferences) {
            Map<Long, TermTranslation> translations = translationsByLanguage.get(language);
            if (translations == null) {
                continue;
            }
            TermTranslation translation = translations.get(termId);
            if (translation != null) {
                return translation;
            }
        }
        return null;
    }

    private static Map<Long, TermTranslation> groupByTermId(List<TermTranslation> translations) {
        return translations.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableMap(
                        translation -> translation.getTerm().getId(),
                        Function.identity()
                ));
    }
}
