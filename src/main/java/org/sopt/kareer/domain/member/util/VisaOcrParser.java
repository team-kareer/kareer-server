package org.sopt.kareer.domain.member.util;

import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.domain.member.entity.enums.VisaType;
import org.sopt.kareer.global.document.util.DocumentDateUtils;
import org.sopt.kareer.global.document.util.DocumentTextUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class VisaOcrParser {

    private static final String DATE_REGEX =
            "(\\d{4}[./-]\\d{1,2}[./-]\\d{1,2}|\\d{1,2}[./-]\\d{1,2}[./-]\\d{4}|\\d{1,2}\\s+[A-Z]{3}\\s+\\d{4})";

    private static final Pattern DATE_PATTERN = Pattern.compile(
            "\\b" + DATE_REGEX + "\\b",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern SUPPORTED_VISA_PATTERN = Pattern.compile(
            "(?i)\\b(D\\s*-?\\s*2|D\\s*-?\\s*10|E\\s*-?\\s*7)\\b"
    );

    private static final Pattern START_DATE_PATTERN = Pattern.compile(
            "(?i)(issue\\s*date|date\\s*of\\s*issue|grant\\s*date|발급일)\\s*[/|:]?\\s*" + DATE_REGEX
    );

    private static final Pattern EXPIRE_DATE_PATTERN = Pattern.compile(
            "(?i)(final\\s*entry\\s*date|expiry\\s*date|expiration\\s*date|valid\\s*until|만료일|입국만료일)\\s*[/|:]?\\s*" + DATE_REGEX
    );

    public VisaInfo parse(String rawText) {
        String text = DocumentTextUtils.normalize(rawText);

        VisaType visaType = extractVisaType(text);
        LocalDate visaStartDate = extractVisaStartDate(text);
        LocalDate visaExpiredAt = extractVisaExpiredAt(text);

        List<LocalDate> allDates = extractAllDates(text);

        if (visaExpiredAt == null) {
            visaExpiredAt = inferExpireDate(allDates, visaStartDate);
        }

        if (visaExpiredAt == null) {
            visaExpiredAt = extractExpireDateFromMrz(text);
        }

        if (visaStartDate == null) {
            visaStartDate = inferStartDate(allDates, visaExpiredAt);
        }

        return new VisaInfo(visaType, visaStartDate, visaExpiredAt);
    }

    private VisaType extractVisaType(String text) {
        Matcher matcher = SUPPORTED_VISA_PATTERN.matcher(text);
        if (matcher.find()) {
            return VisaType.from(matcher.group(1));
        }
        return null;
    }

    private LocalDate extractVisaStartDate(String text) {
        Matcher matcher = START_DATE_PATTERN.matcher(text);
        if (matcher.find()) {
            return DocumentDateUtils.parseDate(matcher.group(2));
        }
        return null;
    }

    private LocalDate extractVisaExpiredAt(String text) {
        Matcher matcher = EXPIRE_DATE_PATTERN.matcher(text);
        if (matcher.find()) {
            return DocumentDateUtils.parseDate(matcher.group(2));
        }
        return null;
    }

    private List<LocalDate> extractAllDates(String text) {
        List<LocalDate> dates = new ArrayList<>();
        Matcher matcher = DATE_PATTERN.matcher(text);

        while (matcher.find()) {
            LocalDate parsed = DocumentDateUtils.parseDate(matcher.group(1));
            if (parsed != null) {
                dates.add(parsed);
            }
        }

        return dates;
    }

    private LocalDate inferStartDate(List<LocalDate> dates, LocalDate expiredAt) {
        if (dates.isEmpty()) {
            return null;
        }

        if (expiredAt != null) {
            return dates.stream()
                    .filter(date -> date.isBefore(expiredAt))
                    .max(LocalDate::compareTo)
                    .orElse(null);
        }

        return dates.stream()
                .min(LocalDate::compareTo)
                .orElse(null);
    }

    private LocalDate inferExpireDate(List<LocalDate> dates, LocalDate startDate) {
        if (dates.isEmpty()) {
            return null;
        }

        if (startDate != null) {
            return dates.stream()
                    .filter(date -> date.isAfter(startDate))
                    .max(LocalDate::compareTo)
                    .orElse(null);
        }

        return dates.stream()
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    private LocalDate extractExpireDateFromMrz(String text) {
        Pattern mrzPattern = Pattern.compile("[MF<](\\d{6})");
        Matcher matcher = mrzPattern.matcher(text);

        while (matcher.find()) {
            LocalDate parsed = DocumentDateUtils.parseLocalDate(matcher.group(1));
            if (parsed != null) {
                return parsed;
            }
        }

        return null;
    }

    public record VisaInfo(
            VisaType visaType,
            LocalDate visaStartDate,
            LocalDate visaExpiredAt
    ) {
    }
}