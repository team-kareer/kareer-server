package org.sopt.kareer.domain.roadmap.service.dto.response;

import org.springframework.ai.document.Document;

import java.util.ArrayList;
import java.util.List;

public record RoadmapGenerationContext(
        String memberContextText,
        List<Document> visaDocs,
        List<Document> careerDocs,
        List<Document> policyDocs
) {
    public List<Document> allDocs() {
        List<Document> all = new ArrayList<>();
        all.addAll(visaDocs);
        all.addAll(careerDocs);
        all.addAll(policyDocs);
        return all;
    }
}
