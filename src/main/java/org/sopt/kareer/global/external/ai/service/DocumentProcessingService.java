package org.sopt.kareer.global.external.ai.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.sopt.kareer.global.external.ai.exception.RagErrorCode;
import org.sopt.kareer.global.external.ai.exception.RagException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class DocumentProcessingService {

    public String extractTextFromPdf(File pdfFile) {

        try (PDDocument document = PDDocument.load(pdfFile)) {
            return new PDFTextStripper().getText(document);
        } catch (IOException e) {
            throw new RagException(RagErrorCode.EXTRACT_TEXT_FAILED);
        }
    }
}
