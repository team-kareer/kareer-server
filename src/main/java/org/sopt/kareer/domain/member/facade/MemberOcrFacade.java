package org.sopt.kareer.domain.member.facade;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.dto.response.OcrPassportResponse;
import org.sopt.kareer.domain.member.dto.response.OcrVisaResponse;
import org.sopt.kareer.domain.member.util.PassportOcrParser;
import org.sopt.kareer.domain.member.util.VisaOcrParser;
import org.sopt.kareer.global.document.exception.DocumentErrorCode;
import org.sopt.kareer.global.document.exception.DocumentException;
import org.sopt.kareer.global.document.service.DocumentProcessingService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class MemberOcrFacade {

    private final DocumentProcessingService documentProcessingService;
    private final VisaOcrParser visaOcrParser;
    private final PassportOcrParser passportOcrParser;

    public OcrVisaResponse extractVisa(MultipartFile file) {
        try {
            String text = documentProcessingService.extractText(file);
            return OcrVisaResponse.from(visaOcrParser.parse(text));
        } catch (DocumentException e) {
            throw e;
        } catch (Exception e) {
            throw new DocumentException(DocumentErrorCode.OCR_PROCESSING_FAILED);
        }
    }

    public OcrPassportResponse extractPassport(MultipartFile file) {
        try {
            String text = documentProcessingService.extractText(file);
            return OcrPassportResponse.from(passportOcrParser.parse(text));
        } catch (DocumentException e) {
            throw e;
        } catch (Exception e) {
            throw new DocumentException(DocumentErrorCode.OCR_PROCESSING_FAILED);
        }
    }
}
