package org.sopt.kareer.domain.document.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.domain.document.dto.response.DocumentUploadResponse;
import org.sopt.kareer.domain.document.entity.RagDocument;
import org.sopt.kareer.domain.document.repository.DocumentVectorStore;
import org.sopt.kareer.domain.document.repository.RagDocumentRepository;
import org.sopt.kareer.domain.document.util.DocumentHashUtil;
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

    private final RagDocumentRepository ragDocumentRepository;

    @Transactional
    public DocumentUploadResponse uploadDocument(MultipartFile file) {

        File tempFile = null;

        try {
            tempFile = File.createTempFile("upload_", ".pdf");
            file.transferTo(tempFile);

            String fileHash = DocumentHashUtil.sha256(tempFile);

            checkDocumentDuplicate(fileHash);

            String originalFilename = file.getOriginalFilename();

            UUID documentUuid = UUID.randomUUID();

            long nowTime = System.currentTimeMillis();

            ragDocumentRepository.save(RagDocument.create(documentUuid, originalFilename, nowTime, fileHash));

            Map<String, Object> docMetadata = new HashMap<>();
            docMetadata.put("originalFilename", originalFilename != null ? originalFilename : "");
            docMetadata.put("uploadTime", nowTime);
            vectorStore.addDocumentFile(documentUuid.toString(), tempFile, docMetadata);

            return DocumentUploadResponse.of(documentUuid.toString());
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

    private void checkDocumentDuplicate(String fileHash) {
        if (ragDocumentRepository.existsByFileHash(fileHash)) {
            throw new DocumentException(DOCUMENT_ALREADY_EXISTS);
        }
    }

}
