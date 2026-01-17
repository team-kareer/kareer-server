package org.sopt.kareer.domain.jobposting.util;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.jobposting.exception.JobPostingException;
import org.sopt.kareer.global.external.ai.service.DocumentProcessingService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

import static org.sopt.kareer.domain.jobposting.exception.JobPostingErrorCode.RESUME_CONTEXT_FAILED;

@Service
@RequiredArgsConstructor
public class ResumeContextService {

    private final DocumentProcessingService documentProcessingService;

    private static final String DEFAULT_RESUME_CONTEXT = """
                   The user did not provide a resume or cover letter.
                   Recommend job postings based only on general suitability for foreigners in Korea.
                   """;

    public String buildContext(List<MultipartFile> files) {

        if (files == null || files.isEmpty()) {
            return DEFAULT_RESUME_CONTEXT;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[RESUME_COVER_LETTER]\n");

        for (MultipartFile file : files) {
            File temp = null;

            try {
                temp = File.createTempFile("resume_", ".pdf");
                file.transferTo(temp);

                String text = documentProcessingService.extractTextWithOcr(temp);

                sb.append("----- FILE START -----\n");
                sb.append(text).append("\n");
                sb.append("----- FILE END -----\n\n");

            } catch (Exception e) {
                throw new JobPostingException(RESUME_CONTEXT_FAILED, e.getMessage());
            } finally {
                if (temp != null && temp.exists()) {
                    temp.delete();
                }
            }
        }

        return normalize(sb.toString());
    }

    private String normalize(String text) {
        int limit = 6000;
        return text.length() > limit ? text.substring(0, limit) : text;
    }
}