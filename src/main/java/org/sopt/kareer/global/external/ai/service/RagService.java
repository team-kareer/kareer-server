package org.sopt.kareer.global.external.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.global.external.ai.dto.response.DocumentUploadResponse;
import org.sopt.kareer.global.external.ai.exception.RagErrorCode;
import org.sopt.kareer.global.external.ai.exception.RagException;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagService {

    private final DocumentProcessingService documentProcessingService;

    private final PgVectorStore documentVectorStore;

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

            documentVectorStore.add(toStore);

            return DocumentUploadResponse.of(UUID.randomUUID().toString());
        } catch (Exception e) {
            throw new RagException(RagErrorCode.EMBEDDING_FAILED, e.getMessage());
        } finally {
            if (temp != null && temp.exists()) temp.delete();
        }

    }

    public List<Document> search(String query, int topK){

        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .build();

        List<Document> documents = documentVectorStore.similaritySearch(request);

        if(documents != null && documents.isEmpty()){
            throw new RagException(RagErrorCode.DOCUMENTS_RETRIEVED_EMPTY);
        }

        return documents;
    }



}
