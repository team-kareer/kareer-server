package org.sopt.kareer.domain.document.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.domain.document.dto.response.DocumentUploadResponse;
import org.sopt.kareer.domain.document.repository.DocumentVectorStore;
import org.sopt.kareer.domain.exception.DocumentException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.sopt.kareer.domain.exception.DocumentErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentVectorStore vectorStore;

    @Transactional
    public DocumentUploadResponse uploadDocument(MultipartFile file) {

        File tempFile = null;

        try {
            tempFile = File.createTempFile("upload_", ".pdf");
            file.transferTo(tempFile);


            String originalFilename = file.getOriginalFilename();

            String documentId = UUID.randomUUID().toString();
            Map<String, Object> docMetadata = new HashMap<>();
            docMetadata.put("originalFilename", originalFilename != null ? originalFilename : "");
            docMetadata.put("uploadTime", System.currentTimeMillis());
            vectorStore.addDocumentFile(documentId, tempFile, docMetadata);
            return DocumentUploadResponse.of(documentId);
        }
        catch(DocumentException e){
                throw e;
        }
        catch(Exception e) {
            throw new DocumentException(DOCUMENT_PROCESSING_FAILED);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }


    }

}
