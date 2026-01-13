package org.sopt.kareer.domain.document.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.domain.document.dto.response.DocumentUploadResponse;
import org.sopt.kareer.domain.document.exception.DocumentErrorCode;
import org.sopt.kareer.domain.document.exception.DocumentException;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ai.document.Document;

import java.io.File;
import java.util.*;

import static org.sopt.kareer.domain.document.exception.DocumentErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {


    private final DocumentProcessingService documentProcessingService;

    private final VectorStore vectorStore;

    @Transactional
    public DocumentUploadResponse uploadDocument(MultipartFile file) {
        File temp = null;

        try {
            temp = File.createTempFile("upload_", ".pdf");
            file.transferTo(temp);

            String text = documentProcessingService.extractTextFromPdf(temp);

            Map<String, Object> baseMeta = new HashMap<>();
            baseMeta.put("originalFilename", Objects.toString(file.getOriginalFilename(), ""));
            baseMeta.put("uploadedAt", System.currentTimeMillis());

            Document doc = new Document(text, baseMeta);

            TokenTextSplitter splitter = TokenTextSplitter.builder()
                    .withChunkSize(512)
                    .withMinChunkSizeChars(350)
                    .withMinChunkLengthToEmbed(5)
                    .withMaxNumChunks(10000)
                    .withKeepSeparator(true)
                    .build();
            List<Document> chunks = splitter.split(doc);

            List<Document> toStore = new ArrayList<>(chunks.size());
            for (int i = 0; i < chunks.size(); i++) {
                Document c = chunks.get(i);
                Map<String, Object> meta = new HashMap<>(c.getMetadata() != null ? c.getMetadata() : Map.of());
                meta.put("chunkIndex", i);

                meta.put("chunkId", UUID.randomUUID().toString());

                toStore.add(new Document(c.getText(), meta));
            }

            vectorStore.add(toStore);

            return DocumentUploadResponse.of(UUID.randomUUID().toString());
        } catch (Exception e) {
            throw new DocumentException(EMBEDDING_FAILED, e.getMessage());
        } finally {
            if (temp != null && temp.exists()) temp.delete();
        }

    }


}
