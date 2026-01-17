package org.sopt.kareer.global.config;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OcrConfig {

    @Bean
    public Tesseract tesseract(@Value("${spring.ocr.tessdataPath}") String tessdataPath) {
        Tesseract tesseract = new Tesseract();
        tesseract.setLanguage("kor+eng");
        tesseract.setDatapath(tessdataPath);

        return tesseract;
    }
}
