package org.sopt.kareer.global.document.service;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.sopt.kareer.global.document.dto.response.PageText;
import org.sopt.kareer.global.document.exception.DocumentErrorCode;
import org.sopt.kareer.global.document.exception.DocumentException;
import org.sopt.kareer.global.external.clova.service.ClovaOcrService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentProcessingService {

    private static final int OCR_DPI = 300;
    private static final int MIN_TEXT_LENGTH = 20;

    private final ClovaOcrService clovaOcrService;

    public String extractText(MultipartFile file) {
        return extractPagesWithOcr(file).stream()
                .map(PageText::text)
                .collect(Collectors.joining("\n"));
    }

    public List<PageText> extractPagesWithOcr(MultipartFile file) {
        validate(file);

        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        try {
            if (isPdf(contentType, filename)) {
                return extractPagesFromPdf(file);
            }

            if (isImage(contentType)) {
                return extractPagesFromImage(file);
            }

            throw new DocumentException(DocumentErrorCode.UNSUPPORTED_FILE_TYPE);
        } catch (DocumentException e) {
            throw e;
        } catch (Exception e) {
            throw new DocumentException(DocumentErrorCode.EXTRACT_TEXT_FAILED, e.getMessage());
        }
    }

    private List<PageText> extractPagesFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            PDFRenderer renderer = new PDFRenderer(document);
            List<PageText> pages = new ArrayList<>();

            for (int i = 1; i <= document.getNumberOfPages(); i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);

                String text = sanitizeText(stripper.getText(document));

                if (text.isBlank() || text.length() < MIN_TEXT_LENGTH) {
                    BufferedImage image = renderer.renderImageWithDPI(i - 1, OCR_DPI);
                    text = sanitizeText(clovaOcrService.doOcr(image));
                }

                if (!text.isBlank()) {
                    pages.add(new PageText(i, text));
                }
            }

            return pages;
        }
    }

    private List<PageText> extractPagesFromImage(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new DocumentException(DocumentErrorCode.INVALID_IMAGE_FILE);
        }

        String text = sanitizeText(clovaOcrService.doOcr(image));

        if (text.isBlank()) {
            return List.of();
        }

        return List.of(new PageText(1, text));
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new DocumentException(DocumentErrorCode.FILE_EMPTY, "파일이 비어 있습니다.");
        }
    }

    private boolean isPdf(String contentType, String filename) {
        return "application/pdf".equalsIgnoreCase(contentType)
               || (filename != null && filename.toLowerCase().endsWith(".pdf"));
    }

    private boolean isImage(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }

    private static String sanitizeText(String s) {
        return s == null ? "" :
                s.replace("\u0000", "")
                        .replaceAll("[\\x01-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", " ")
                        .replace('\uFFFD', ' ')
                        .replaceAll("\\s+", " ")
                        .trim();
    }
}