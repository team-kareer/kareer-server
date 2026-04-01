package org.sopt.kareer.domain.roadmap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.domain.roadmap.dto.translation.RoadmapTranslationTarget;
import org.sopt.kareer.global.external.google.service.GoogleTranslationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoadmapTranslationService {

    private static final List<String> TARGET_LANGUAGES = List.of("en", "vi", "zh-CN");

    private final GoogleTranslationService googleTranslationService;
    private final RoadmapTranslationPersistService persistService;
    private final ExecutorService executorService;

    public void translateAllLanguages(RoadmapTranslationTarget target) {
        if (target.phases().isEmpty()) return;

        List<CompletableFuture<Void>> futures = TARGET_LANGUAGES.stream()
                .map(language -> CompletableFuture.runAsync(() -> {
                    try {
                        RoadmapTranslationTarget translated = googleTranslationService.translate(target, language);
                        persistService.saveTranslations(translated, language);
                    } catch (Exception e) {
                        log.error("[TRANSLATION] failed: language={}", language, e);
                    }
                }, executorService))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
}
