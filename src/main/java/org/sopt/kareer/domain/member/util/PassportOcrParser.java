package org.sopt.kareer.domain.member.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.domain.member.entity.enums.Country;
import org.sopt.kareer.global.document.util.DocumentDateUtils;
import org.sopt.kareer.global.document.util.DocumentTextUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class PassportOcrParser {

    private static final int TD3_LENGTH = 44;

    private final CountryResolver countryResolver;

    public PassportInfo parse(String rawText) {
        log.info("passport rawText: {}", rawText);

        String text = DocumentTextUtils.normalize(rawText);
        Mrz mrz = extractMrz(text);

        if (mrz == null) {
            return new PassportInfo(null, null, null);
        }

        log.info("mrz line1={}, line2={}", mrz.line1(), mrz.line2());

        String fullName = extractName(mrz.line1());
        Country country = extractCountry(mrz.line1(), mrz.line2());
        LocalDate birthDate = extractBirth(mrz.line2());

        return new PassportInfo(fullName, country, birthDate);
    }

    private Mrz extractMrz(String text) {
        List<String> candidates = collectCandidates(text);

        String line1 = null;
        String line2 = null;

        for (String c : candidates) {

            if (line1 == null && isLine1(c)) {
                line1 = normalizeLineLength(c);
                continue;
            }

            if (line2 == null && isLine2(c)) {
                line2 = normalizeLineLength(c);
            }

            if (line1 != null && line2 != null) break;
        }

        if (line1 == null && line2 == null) return null;

        return new Mrz(line1, line2);
    }

    private List<String> collectCandidates(String text) {
        List<String> result = new ArrayList<>();
        String upper = text.toUpperCase(Locale.ROOT);

        for (String token : upper.split("\\s+")) {
            String cleaned = sanitize(token);
            if (cleaned.length() >= 20) result.add(cleaned);
        }

        Matcher m = Pattern.compile("[A-Z0-9<]{20,}").matcher(upper);
        while (m.find()) {
            result.add(sanitize(m.group()));
        }

        return result;
    }

    private String sanitize(String v) {
        return v.replaceAll("[^A-Z0-9<]", "");
    }

    private boolean isLine1(String v) {
        if (v == null || v.length() < 20) return false;

        String fitted = normalizeLineLength(v);

        if (fitted.charAt(0) != 'P') return false;

        // 3~5: 국가코드
        if (!substringSafely(fitted, 2, 5).matches("[A-Z]{3}")) return false;

        return fitted.contains("<<");
    }

    private boolean isLine2(String v) {
        if (v == null || v.length() < 20) return false;

        String fitted = normalizeLineLength(v);

        // 11~13: 국가코드
        if (!substringSafely(fitted, 10, 13).matches("[A-Z]{3}")) return false;

        // 14~19: 생년월일
        return substringSafely(fitted, 13, 19).matches("\\d{6}");
    }

    private String normalizeLineLength(String v) {
        if (v.length() >= TD3_LENGTH) {
            return v.substring(0, TD3_LENGTH);
        }
        return v + "<".repeat(TD3_LENGTH - v.length());
    }

    private String substringSafely(String v, int s, int e) {
        if (v.length() < e) return "";
        return v.substring(s, e);
    }

    private String extractName(String line1) {
        if (line1 == null) return null;

        try {
            String fitted = normalizeLineLength(line1);

            // 반드시 P로 시작해야 함
            if (fitted.charAt(0) != 'P') return null;

            // 국가코드 이후부터 이름
            String body = fitted.substring(5);

            String[] parts = body.split("<<", 2);
            if (parts.length < 2) return null;

            String surname = normalizeName(parts[0]);
            String given = normalizeName(parts[1]);

            String name = (surname + " " + given).trim();
            return name.isBlank() ? null : name;

        } catch (Exception e) {
            return null;
        }
    }

    private Country extractCountry(String line1, String line2) {
        try {
            if (line1 != null) {
                String fitted = normalizeLineLength(line1);
                String code = substringSafely(fitted, 2, 5);
                Country c = countryResolver.resolveIso3(code);
                if (c != null) return c;
            }

            if (line2 != null) {
                String fitted = normalizeLineLength(line2);
                String code = substringSafely(fitted, 10, 13);
                return countryResolver.resolveIso3(code);
            }

            return null;

        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate extractBirth(String line2) {
        try {
            if (line2 == null) return null;

            String fitted = normalizeLineLength(line2);
            String birth = substringSafely(fitted, 13, 19);

            if (!birth.matches("\\d{6}")) return null;

            return DocumentDateUtils.parseYYMMDD(birth);

        } catch (Exception e) {
            return null;
        }
    }

    private String normalizeName(String v) {
        return v.replace("<", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private record Mrz(String line1, String line2) {}

    public record PassportInfo(
            String fullName,
            Country country,
            LocalDate birthDate
    ) {}
}