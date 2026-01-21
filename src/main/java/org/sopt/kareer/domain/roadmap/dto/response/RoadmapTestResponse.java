package org.sopt.kareer.domain.roadmap.dto.response;

import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

import static java.util.stream.IntStream.range;

public record RoadmapTestResponse(
        RoadmapResponse roadmap,
        List<RetrievedChunk> retrieved
) {
    public static RoadmapTestResponse of(RoadmapResponse roadmap, List<Document> docs) {
        return new RoadmapTestResponse(roadmap, RetrievedChunk.from(docs));
    }

    public record RetrievedChunk(
            int index,
            String textPreview,
            Map<String, Object> metadata
    ) {
        private static final int PREVIEW_LIMIT = 600;

        public static List<RetrievedChunk> from(List<Document> docs) {
            if (docs == null) return List.of();

            return range(0, docs.size())
                    .mapToObj(i -> {
                        Document d = docs.get(i);
                        String text = d.getText() == null ? "" : d.getText();
                        String preview = text.length() > PREVIEW_LIMIT
                                ? text.substring(0, PREVIEW_LIMIT) + "..."
                                : text;

                        return new RetrievedChunk(i, preview, d.getMetadata());
                    })
                    .toList();
        }
    }
}
