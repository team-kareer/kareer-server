package org.sopt.kareer.domain.document.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sopt.kareer.domain.exception.DocumentErrorCode;
import org.sopt.kareer.domain.exception.DocumentException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class DocumentProcessingService {

    public String extractTextFromPdf(File pdfFile) {

        try (PDDocument document = PDDocument.load(pdfFile)) {
            return new PDFTextStripper().getText(document);
        } catch (IOException e) {
            throw new DocumentException(DocumentErrorCode.EXTRACT_TEXT_FAILED);
        }
    }
}
