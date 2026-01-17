package org.sopt.kareer.global.external.ai.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.sopt.kareer.global.external.ai.exception.RagErrorCode;
import org.sopt.kareer.global.external.ai.exception.RagException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentProcessingService {

    public String extractTextFromPdf(File pdfFile) {

        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            String raw = stripper.getText(document);

            return sanitizeText(raw);
        } catch (IOException e) {
            throw new RagException(RagErrorCode.EXTRACT_TEXT_FAILED);
        }
    }

    public List<PageText> extractPageFromPdf(File pdfFile) {
        try(PDDocument document = PDDocument.load(pdfFile)){
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            int totalPages = document.getNumberOfPages();
            List<PageText> pages = new ArrayList<>(totalPages);

            for(int i = 1; i <= totalPages; i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);

                String rawText = stripper.getText(document);
                String cleanedText = sanitizeText(rawText);

                if (!cleanedText.isBlank()) {
                    pages.add(new PageText(i, cleanedText));
                }
            }
            return pages;

        } catch (IOException e){
            throw new RagException(RagErrorCode.EXTRACT_TEXT_FAILED, e.getMessage());
        }
    }

    private static String sanitizeText(String s){
        return s == null ? "" :
                s.replace("\u0000", "")
                        .replaceAll("[\\x01-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", " ")
                        .replace('\uFFFD', ' ');
    }



    public record PageText(int pageNumber, String text) {}
}
