package org.sopt.kareer.global.external.clova.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.external.ai.exception.RagErrorCode;
import org.sopt.kareer.global.external.ai.exception.RagException;
import org.sopt.kareer.global.external.clova.dto.request.ClovaOcrRequest;
import org.sopt.kareer.global.external.clova.dto.response.ClovaOcrResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClovaOcrService {

    private final WebClient clovaOcrWebClient;

    @Value("${spring.clova.ocr.timeout-ms:15000}")
    private long timeoutMs;

    public String doOcr(BufferedImage image) {
        try {
            String base64 = Base64.getEncoder().encodeToString(toJpgBytes(image));

            ClovaOcrRequest body = new ClovaOcrRequest(
                    "V2",
                    UUID.randomUUID().toString(),
                    System.currentTimeMillis(),
                    List.of(new ClovaOcrRequest.Image("jpg", "page", base64))
            );

            ClovaOcrResponse response = clovaOcrWebClient.post()
                    .uri("")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(ClovaOcrResponse.class)
                    .block(Duration.ofMillis(timeoutMs));

            if (response == null || response.images() == null || response.images().isEmpty()) return "";

            var fields = response.images().get(0).fields();
            if (fields == null || fields.isEmpty()) return "";

            return fields.stream()
                    .map(ClovaOcrResponse.Field::inferText)
                    .filter(s -> s != null && !s.isBlank())
                    .map(String::trim)
                    .collect(Collectors.joining(" "));

        } catch (Exception e) {
            throw new RagException(
                    RagErrorCode.EXTRACT_IMAGE_FAILED,
                    "CLOVA OCR failed: " + e.getMessage()
            );
        }
    }

    private byte[] toJpgBytes(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RagException(RagErrorCode.EXTRACT_IMAGE_FAILED, "Image encoding failed: " + e.getMessage());
        }
    }
}