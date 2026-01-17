package org.sopt.kareer.global.external.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.sopt.kareer.global.external.ai.exception.RagErrorCode;
import org.sopt.kareer.global.external.ai.exception.RagException;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentProcessingService {

    private static final double MIN_TEXT_PAGE_RATIO = 0.2;
    private static final int MIN_TOTAL_TEXT_CHARS = 300;
    private static final int OCR_DPI = 300;

    private final Tesseract tesseract;

    public List<PageText> extractPagesWithOcr(File pdfFile) {
        List<PageText> textPages = extractPageFromPdf(pdfFile);

        int totalPages = getTotalPages(pdfFile);

        int totalChars = textPages.stream().mapToInt(p -> p.text().length()).sum();
        boolean tooFewPages = textPages.size() < Math.max(1, (int) Math.ceil(totalPages * MIN_TEXT_PAGE_RATIO));
        boolean tooShort = totalChars < MIN_TOTAL_TEXT_CHARS;

        if (tooFewPages || tooShort) {
            return extractTextFromImage(pdfFile);
        }
        return textPages;
    }

    public String extractTextWithOcr(File pdfFile) {
        return extractPagesWithOcr(pdfFile).stream()
                .map(PageText::text)
                .reduce("", (a, b) -> a + "\n" + b);
    }

    private int getTotalPages(File pdfFile) {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            return document.getNumberOfPages();
        } catch (IOException e) {
            throw new RagException(RagErrorCode.EXTRACT_TEXT_FAILED, e.getMessage());
        }
    }

    public List<PageText> extractPageFromPdf(File pdfFile) {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            int totalPages = document.getNumberOfPages();
            List<PageText> pages = new ArrayList<>(totalPages);

            for (int i = 1; i <= totalPages; i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);

                String cleanedText = sanitizeText(stripper.getText(document));
                if (!cleanedText.isBlank()) {
                    pages.add(new PageText(i, cleanedText));
                }
            }
            return pages;
        } catch (IOException e) {
            throw new RagException(RagErrorCode.EXTRACT_TEXT_FAILED, e.getMessage());
        }
    }

    private static String sanitizeText(String s) {
        return s == null ? "" :
                s.replace("\u0000", "")
                        .replaceAll("[\\x01-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", " ")
                        .replace('\uFFFD', ' ');
    }

    private List<PageText> extractTextFromImage(File pdfFile) {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFRenderer renderer = new PDFRenderer(document);

            int totalPages = document.getNumberOfPages();
            List<PageText> pages = new ArrayList<>(totalPages);

            for (int i = 0; i < totalPages; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, OCR_DPI);
                String cleaned = sanitizeText(tesseract.doOCR(image));
                log.info("OCR 작동 : " + cleaned);
                if (!cleaned.isBlank()) {
                    pages.add(new PageText(i + 1, cleaned));
                }
            }
            return pages;

        } catch (Exception e) {
            throw new RagException(RagErrorCode.EXTRACT_IMAGE_FAILED, e.getMessage());
        }
    }

    public record PageText(int pageNumber, String text) {}
}
