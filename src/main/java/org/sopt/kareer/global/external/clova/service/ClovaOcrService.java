package org.sopt.kareer.global.external.clova.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.global.external.clova.dto.request.ClovaOcrRequest;
import org.sopt.kareer.global.external.clova.dto.response.ClovaOcrResponse;
import org.sopt.kareer.global.external.clova.exception.ClovaErrorCode;
import org.sopt.kareer.global.external.clova.exception.ClovaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClovaOcrService {

    private static final String OCR_IMAGE_FORMAT = "png";

    private final WebClient clovaOcrWebClient;

    @Value("${spring.clova.ocr.timeout-ms:15000}")
    private long timeoutMs;

    public String doOcr(BufferedImage image) {
        try {
            byte[] imageBytes = toOcrBytes(image);
            String base64 = Base64.getEncoder().encodeToString(imageBytes);

            log.info("CLOVA OCR request image size: {} bytes", imageBytes.length);

            ClovaOcrRequest body = new ClovaOcrRequest(
                    "V2",
                    UUID.randomUUID().toString(),
                    System.currentTimeMillis(),
                    List.of(new ClovaOcrRequest.Image(OCR_IMAGE_FORMAT, "page", base64))
            );

            ClovaOcrResponse response = clovaOcrWebClient.post()
                    .uri("")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .exchangeToMono(clientResponse -> {
                        HttpStatusCode status = clientResponse.statusCode();

                        if (status.is2xxSuccessful()) {
                            return clientResponse.bodyToMono(ClovaOcrResponse.class);
                        }

                        return clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(errorBody -> {
                                    log.error("CLOVA OCR error response. status={}, body={}",
                                            status.value(), errorBody);

                                    return Mono.error(
                                            new ClovaException(
                                                    ClovaErrorCode.EXTRACT_IMAGE_FAILED,
                                                    "CLOVA OCR error. status=" + status.value() + ", body=" + errorBody
                                            )
                                    );
                                });
                    })
                    .block(Duration.ofMillis(timeoutMs));

            if (response == null || response.images() == null || response.images().isEmpty()) {
                log.warn("CLOVA OCR response is empty");
                return "";
            }

            var fields = response.images().get(0).fields();
            if (fields == null || fields.isEmpty()) {
                log.warn("CLOVA OCR fields are empty");
                return "";
            }

            return fields.stream()
                    .map(ClovaOcrResponse.Field::inferText)
                    .filter(text -> text != null && !text.isBlank())
                    .map(String::trim)
                    .collect(Collectors.joining(" "));

        } catch (ClovaException e) {
            throw e;
        } catch (Exception e) {
            log.error("CLOVA OCR failed", e);
            throw new ClovaException(
                    ClovaErrorCode.EXTRACT_IMAGE_FAILED,
                    "CLOVA OCR failed: " + e.getMessage()
            );
        }
    }

    private byte[] toOcrBytes(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage normalized = normalizeImage(image);

            boolean success = ImageIO.write(normalized, OCR_IMAGE_FORMAT, baos);
            if (!success || baos.size() == 0) {
                throw new ClovaException(
                        ClovaErrorCode.EXTRACT_IMAGE_FAILED,
                        "Image encoding failed"
                );
            }

            return baos.toByteArray();
        } catch (ClovaException e) {
            throw e;
        } catch (Exception e) {
            throw new ClovaException(
                    ClovaErrorCode.EXTRACT_IMAGE_FAILED,
                    "Image encoding failed: " + e.getMessage()
            );
        }
    }

    /**
     * 모든 입력 이미지를 OCR 전송용 표준 RGB 이미지로 정규화한다.
     */
    private BufferedImage normalizeImage(BufferedImage source) {
        if (source == null) {
            throw new ClovaException(
                    ClovaErrorCode.EXTRACT_IMAGE_FAILED,
                    "Image is null"
            );
        }

        BufferedImage target = new BufferedImage(
                source.getWidth(),
                source.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = target.createGraphics();
        try {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, target.getWidth(), target.getHeight());
            g.drawImage(source, 0, 0, null);
        } finally {
            g.dispose();
        }

        return target;
    }
}