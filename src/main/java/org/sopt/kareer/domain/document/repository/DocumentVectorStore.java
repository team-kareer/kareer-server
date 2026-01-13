package org.sopt.kareer.domain.document.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.document.entity.RagDocument;
import org.sopt.kareer.domain.document.entity.RagDocumentChunk;
import org.sopt.kareer.domain.document.service.DocumentProcessingService;
import org.sopt.kareer.domain.document.service.EmbeddingService;
import org.sopt.kareer.domain.document.service.TextSplitterService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DocumentVectorStore {

    private final RagDocumentChunkRepository ragDocumentChunkRepository;
    private final RagDocumentRepository ragDocumentRepository;

    private final DocumentProcessingService documentProcessingService;
    private final TextSplitterService textSplitterService;
    private final EmbeddingService embeddingService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void addDocumentFile(String documentId, File file, Map<String, Object> docMetadata) {
        UUID docUuid = UUID.fromString(documentId);

        String originalFilename = Objects.toString(docMetadata.getOrDefault("originalFilename", ""), "");
        long uploadTime = (long) docMetadata.getOrDefault("uploadTime", System.currentTimeMillis());


        String text = documentProcessingService.extractTextFromPdf(file);

        List<String> chunks = textSplitterService.split(text);

        for (int i = 0; i < chunks.size(); i++) {

            String chunkText = chunks.get(i);

            float[] embedding = embeddingService.getEmbeddingModel()
                            .embed(chunkText);

            String embeddingLiteral = toVectorLiteral(embedding);

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("originalFilename", originalFilename);
            metadata.put("uploadTime", uploadTime);
            metadata.put("chunkIndex", i);

            String metadataJson;
            try {
                metadataJson = objectMapper.writeValueAsString(metadata);
            } catch (Exception e) {
                metadataJson = "{}";
            }

            ragDocumentChunkRepository.insertChunk(UUID.randomUUID(), docUuid, i, chunkText, metadataJson, embeddingLiteral);
        }
    }

    private static String toVectorLiteral(float[] v) {
        StringBuilder sb = new StringBuilder(v.length * 8);
        sb.append('[');
        for (int i = 0; i < v.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(v[i]);
        }
        sb.append(']');
        return sb.toString();
    }


}
