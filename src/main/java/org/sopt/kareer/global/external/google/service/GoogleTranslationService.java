package org.sopt.kareer.global.external.google.service;

import org.sopt.kareer.domain.roadmap.dto.translation.RoadmapTranslationTarget;
import org.sopt.kareer.global.external.google.exception.GoogleTranslationErrorCode;
import org.sopt.kareer.global.external.google.exception.GoogleTranslationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GoogleTranslationService {

    private final RestClient restClient;
    private final String translateUrl;
    private final String apiKey;

    public GoogleTranslationService(
            @Value("${google.translate.url}") String translateUrl,
            @Value("${google.translate.api-key}") String apiKey) {
        this.restClient = RestClient.create();
        this.translateUrl = translateUrl;
        this.apiKey = apiKey;
    }

    public RoadmapTranslationTarget translate(RoadmapTranslationTarget target, String language) {
        List<String> texts = new ArrayList<>();
        extractTexts(target, texts);

        List<String> translated = callTranslateApi(texts, language);

        int[] idx = {0};
        return reassemble(target, translated, idx);
    }

    private void extractTexts(RoadmapTranslationTarget target, List<String> texts) {
        for (RoadmapTranslationTarget.PhaseTarget phase : target.phases()) {
            texts.add(phase.goal());
            texts.add(phase.description());
            for (RoadmapTranslationTarget.PhaseActionTarget action : phase.actions()) {
                texts.add(action.title());
                texts.add(action.description());
                texts.add(action.importance());
                for (RoadmapTranslationTarget.GuidelineTarget g : action.guidelines()) texts.add(g.content());
                for (RoadmapTranslationTarget.MistakeTarget m : action.mistakes()) texts.add(m.content());
                for (RoadmapTranslationTarget.ActionItemTarget i : action.actionItems()) texts.add(i.title());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> callTranslateApi(List<String> texts, String targetLang) {
        Map<String, Object> request = Map.of(
                "q", texts,
                "target", targetLang,
                "format", "text"
        );

        Map<String, Object> response = restClient.post()
                .uri(translateUrl, apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new GoogleTranslationException(GoogleTranslationErrorCode.TRANSLATION_API_INVALID_RESPONSE, "Translation API returned null response");
        }

        Map<String, Object> data = (Map<String, Object>) response.get("data");
        if (data == null) {
            throw new GoogleTranslationException(GoogleTranslationErrorCode.TRANSLATION_API_INVALID_RESPONSE, "Translation API response missing 'data' field");
        }

        List<Map<String, String>> translations = (List<Map<String, String>>) data.get("translations");
        if (translations == null) {
            throw new GoogleTranslationException(GoogleTranslationErrorCode.TRANSLATION_API_INVALID_RESPONSE, "Translation API returned invalid translations count");
        }

        return translations.stream().map(t -> t.get("translatedText")).toList();
    }

    private RoadmapTranslationTarget reassemble(RoadmapTranslationTarget original, List<String> translated, int[] idx) {
        List<RoadmapTranslationTarget.PhaseTarget> phases = new ArrayList<>();
        for (RoadmapTranslationTarget.PhaseTarget orig : original.phases()) {
            String goal = translated.get(idx[0]++);
            String description = translated.get(idx[0]++);

            List<RoadmapTranslationTarget.PhaseActionTarget> actions = new ArrayList<>();
            for (RoadmapTranslationTarget.PhaseActionTarget oa : orig.actions()) {
                String title = translated.get(idx[0]++);
                String actionDesc = translated.get(idx[0]++);
                String importance = translated.get(idx[0]++);

                List<RoadmapTranslationTarget.GuidelineTarget> guidelines = new ArrayList<>();
                for (RoadmapTranslationTarget.GuidelineTarget g : oa.guidelines()) {
                    guidelines.add(new RoadmapTranslationTarget.GuidelineTarget(g.guidelineId(), translated.get(idx[0]++)));
                }

                List<RoadmapTranslationTarget.MistakeTarget> mistakes = new ArrayList<>();
                for (RoadmapTranslationTarget.MistakeTarget m : oa.mistakes()) {
                    mistakes.add(new RoadmapTranslationTarget.MistakeTarget(m.mistakeId(), translated.get(idx[0]++)));
                }

                List<RoadmapTranslationTarget.ActionItemTarget> actionItems = new ArrayList<>();
                for (RoadmapTranslationTarget.ActionItemTarget item : oa.actionItems()) {
                    actionItems.add(new RoadmapTranslationTarget.ActionItemTarget(item.actionItemId(), translated.get(idx[0]++)));
                }

                actions.add(new RoadmapTranslationTarget.PhaseActionTarget(
                        oa.phaseActionId(), title, actionDesc, importance, guidelines, mistakes, actionItems));
            }

            phases.add(new RoadmapTranslationTarget.PhaseTarget(orig.phaseId(), goal, description, actions));
        }
        return new RoadmapTranslationTarget(phases);
    }
}
