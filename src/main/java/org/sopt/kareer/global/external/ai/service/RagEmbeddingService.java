package org.sopt.kareer.global.external.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.domain.jobposting.entity.JobPosting;
import org.sopt.kareer.domain.jobposting.exception.JobPostingErrorCode;
import org.sopt.kareer.domain.jobposting.exception.JobPostingException;
import org.sopt.kareer.domain.jobposting.repository.JobPostingRepository;
import org.sopt.kareer.global.external.ai.builder.JobPostingEmbeddingTextBuilder;
import org.sopt.kareer.global.external.ai.dto.response.RequiredSection;
import org.sopt.kareer.global.external.ai.enums.RequiredCategory;
import org.sopt.kareer.global.external.ai.exception.RagErrorCode;
import org.sopt.kareer.global.external.ai.exception.RagException;
import org.sopt.kareer.global.external.ai.util.OcrTextNormalizer;
import org.sopt.kareer.global.external.ai.util.RequiredPdfParser;
import org.sopt.kareer.global.external.clova.service.DocumentProcessingService;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

import static org.sopt.kareer.global.external.ai.constant.RequiredDocumentConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagEmbeddingService {

    private final DocumentProcessingService documentProcessingService;

    private final PgVectorStore policyDocumentVectorStore;

    private final PgVectorStore jobPostingVectorStore;

    private final JobPostingRepository jobPostingRepository;

    private final PgVectorStore requiredDocumentVectorStore;

    private final RequiredPdfParser requiredPdfParser = new RequiredPdfParser();

    @Transactional
    public void uploadPolicyDocument(List<MultipartFile> files) {
        uploadDocument(files, policyDocumentVectorStore);
    }


    @Transactional
    public void embedJobPosting(List<Long> jobPostingIds) {

        for (Long jobPostingId : jobPostingIds) {
            JobPosting jobPosting = jobPostingRepository.findById(jobPostingId).orElseThrow(() -> new JobPostingException(JobPostingErrorCode.JOB_POSTING_NOT_FOUND));

            try{
                String embeddingText = JobPostingEmbeddingTextBuilder.buildEmbeddingText(jobPosting);

                Map<String, Object> baseMeta = new HashMap<>();

                baseMeta.put("jobPostingId", jobPosting.getId());

                List<Document> toStore = getDocuments(embeddingText, baseMeta);

                jobPostingVectorStore.add(toStore);


            } catch (Exception e){
                throw new RagException(RagErrorCode.EMBEDDING_FAILED, e.getMessage());
            }
        }

    }

    private void uploadDocument(List<MultipartFile> files, PgVectorStore targetStore) {
        File temp = null;

        for (MultipartFile file : files) {
            try {
                temp = File.createTempFile("upload_", ".pdf");
                file.transferTo(temp);

                Map<String, Object> baseMeta = new HashMap<>();
                baseMeta.put("originalFilename", Objects.toString(file.getOriginalFilename(), ""));
                baseMeta.put("uploadedAt", System.currentTimeMillis());

                var pages = documentProcessingService.extractPagesWithOcr(temp);

                List<Document> toStore = new ArrayList<>();
                for (var page : pages) {
                    Map<String, Object> pageMeta = new HashMap<>(baseMeta);
                    pageMeta.put("page", page.pageNumber());

                    toStore.addAll(getDocuments(page.text(), pageMeta));
                }

                targetStore.add(toStore);

            } catch (Exception e) {
                throw new RagException(RagErrorCode.EMBEDDING_FAILED, e.getMessage());
            } finally {
                if (temp != null && temp.exists()) temp.delete();
            }
        }
    }

    @Transactional
    public void uploadRequiredDocument(MultipartFile file, RequiredCategory requiredCategory) {
        uploadAndIngest(file, requiredCategory.getDescription(), requiredCategory);
    }

    private void uploadAndIngest(MultipartFile file, String source, RequiredCategory category) {
        if (file == null || file.isEmpty()) return;

        File temp = null;
        try {
            temp = File.createTempFile("upload_", ".pdf");
            file.transferTo(temp);

            var pages = documentProcessingService.extractPagesWithOcr(temp);
            StringBuilder full = new StringBuilder();
            for (var p : pages) {
                full.append(p.text()).append("\n");
            }
            String fullText = full.toString();
            fullText = OcrTextNormalizer.normalize(fullText);
            fullText = OcrTextNormalizer.forceNewlines(fullText);

            List<RequiredSection> sections = (category == RequiredCategory.VISA)
                    ? requiredPdfParser.parseVisa(fullText)
                    : requiredPdfParser.parseCareer(fullText);

            List<Document> toStore = new ArrayList<>();
            for (RequiredSection s : sections) {
                Map<String, Object> meta = new HashMap<>();
                meta.put(CATEGORY, s.category().name());
                meta.put(DOMAIN, s.domain());
                meta.put(CASE_NO, s.caseNo());
                meta.put(LABEL, s.depth().getLabel());
                meta.put(TITLE, s.title());
                meta.put(SOURCE, source);

                toStore.addAll(getDocuments(s.text(), meta));
            }

            requiredDocumentVectorStore.add(toStore);

        } catch (Exception e) {
            throw new RagException(RagErrorCode.EMBEDDING_FAILED, e.getMessage());
        } finally {
            if (temp != null && temp.exists()) temp.delete();
        }

    }

    private List<Document> getDocuments(String text, Map<String, Object> baseMeta) {
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
            Map<String, Object> meta = new HashMap<>(c.getMetadata());
            meta.put("chunkIndex", i);

            meta.put("chunkId", UUID.randomUUID().toString());

            toStore.add(new Document(c.getText(), meta));
        }
        return toStore;
    }

}
