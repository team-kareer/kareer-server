package org.sopt.kareer.domain.member.util;

import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.domain.member.entity.enums.VisaType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class VisaOcrParser {

    private static final Pattern DATE_PATTERN = Pattern.compile(
            "\\b(" +
            "\\d{4}[./-]\\d{1,2}[./-]\\d{1,2}" +
            "|" +
            "\\d{1,2}[./-]\\d{1,2}[./-]\\d{4}" +
            "|" +
            "\\d{1,2}\\s+[A-Z]{3}\\s+\\d{4}" +
            ")\\b",
            Pattern.CASE_INSENSITIVE
    );

    // 비자유형 파싱용
    private static final Pattern STATUS_PATTERN = Pattern.compile(
            "(?i)(status|체류자격)\\s*[:：]?\\s*([A-Z]\\s*-?\\s*\\d{1,2})"
    );

    private static final Pattern START_DATE_PATTERN = Pattern.compile(
            "(?i)(issue\\s*date|date\\s*of\\s*issue|grant\\s*date|issued\\s*on|발급일)\\s*[:：]?\\s*([0-9./\\- ]{8,20}|\\d{1,2}\\s+[A-Z]{3}\\s+\\d{4})"
    );

    private static final Pattern EXPIRE_DATE_PATTERN = Pattern.compile(
            "(?i)(final\\s*entry\\s*date|expiry\\s*date|expiration\\s*date|valid\\s*until|until|만료일)\\s*[:：]?\\s*([0-9./\\- ]{8,20}|\\d{1,2}\\s+[A-Z]{3}\\s+\\d{4})"
    );

    public VisaInfo parse(String rawText) {
        String text = normalize(rawText);

        VisaType visaType = extractVisaType(text);
        LocalDate visaStartDate = extractVisaStartDate(text);
        LocalDate visaExpiredAt = extractVisaExpiredAt(text);

        List<LocalDate> allDates = extractAllDates(text);

        if (visaStartDate == null) {
            visaStartDate = inferStartDate(allDates, visaExpiredAt);
        }

        if (visaExpiredAt == null) {
            visaExpiredAt = inferExpireDate(allDates, visaStartDate);
        }

        if (visaExpiredAt == null) {
            visaExpiredAt = extractExpireDateFromMrz(text);
        }

        return new VisaInfo(visaType, visaStartDate, visaExpiredAt);
    }

    private String normalize(String text) {
        return text == null ? "" : text.replaceAll("\\s+", " ").trim();
    }

    private VisaType extractVisaType(String text) {
        Matcher matcher = STATUS_PATTERN.matcher(text);
        if (matcher.find()) {
            VisaType visaType = VisaType.from(matcher.group(2));
            if (visaType != null) {
                return visaType;
            }
        }

        Pattern fallbackPattern = Pattern.compile("\\b([A-Z]\\s*-?\\s*\\d{1,2})\\b");
        Matcher fallbackMatcher = fallbackPattern.matcher(text);

        while (fallbackMatcher.find()) {
            VisaType visaType = VisaType.from(fallbackMatcher.group(1));
            if (visaType != null) {
                return visaType;
            }
        }

        return null;
    }

    private LocalDate extractVisaStartDate(String text) {
        Matcher matcher = START_DATE_PATTERN.matcher(text);
        if (matcher.find()) {
            return parseDate(matcher.group(2));
        }
        return null;
    }

    private LocalDate extractVisaExpiredAt(String text) {
        Matcher matcher = EXPIRE_DATE_PATTERN.matcher(text);
        if (matcher.find()) {
            return parseDate(matcher.group(2));
        }
        return null;
    }

    private List<LocalDate> extractAllDates(String text) {
        List<LocalDate> dates = new ArrayList<>();
        Matcher matcher = DATE_PATTERN.matcher(text);

        while (matcher.find()) {
            LocalDate parsed = parseDate(matcher.group(1));
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
                    .filter(date -> !date.isAfter(expiredAt))
                    .min(LocalDate::compareTo)
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
                    .filter(date -> !date.isBefore(startDate))
                    .max(LocalDate::compareTo)
                    .orElse(null);
        }

        return dates.stream()
                .max(LocalDate::compareTo)
                .orElse(null);
    }

    private LocalDate extractExpireDateFromMrz(String text) {
        Pattern mrzPattern = Pattern.compile("[MF<](\\d{6})");
        Matcher matcher = mrzPattern.matcher(text);

        while (matcher.find()) {
            LocalDate parsed = parseYYMMDD(matcher.group(1));
            if (parsed != null) {
                return parsed;
            }
        }

        return null;
    }

    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        String value = raw.trim().toUpperCase(Locale.ROOT).replaceAll("\\s+", " ");

        DateTimeFormatter[] formatters = new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("yyyy.MM.dd"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("dd.MM.yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(value, formatter);
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private LocalDate parseYYMMDD(String value) {
        try {
            int year = Integer.parseInt(value.substring(0, 2));
            int month = Integer.parseInt(value.substring(2, 4));
            int day = Integer.parseInt(value.substring(4, 6));

            year += (year >= 50 ? 1900 : 2000);

            return LocalDate.of(year, month, day);
        } catch (Exception e) {
            return null;
        }
    }

    public record VisaInfo(
            VisaType visaType,
            LocalDate visaStartDate,
            LocalDate visaExpiredAt
    ) {
    }
}
