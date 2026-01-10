package org.sopt.kareer.domain.jobposting.util;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static org.sopt.kareer.domain.jobposting.constant.JobPostingCrawlConstants.*;

public class CrawledTextUtil {

    public static LocalDate extractDeadline(String pageText) {
        if (pageText == null) return null;

        for (String line : pageText.split("\\R")) {
            String t = line.trim();
            if (!t.startsWith(DEADLINE_PREFIX)) continue;

            Matcher matcher = DEADLINE_DATE_PATTERN.matcher(t);
            if (matcher.find()) {
                int year = 2000 + Integer.parseInt(matcher.group(1));
                int month = Integer.parseInt(matcher.group(2));
                int day = Integer.parseInt(matcher.group(3));
                return LocalDate.of(year, month, day);
            }
        }
        return null;
    }

    public static Map<String, String> extractJobSummaryFields(String pageText) {
        Map<String, String> map = new LinkedHashMap<>();
        if (pageText == null || pageText.isBlank()) return map;

        List<String> lines = Arrays.stream(pageText.split("\\R"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        for (int i = 0; i < lines.size() - 1; i++) {
            String line = lines.get(i);
            if (JOBPLOY_LABELS.contains(line)) {
                String next = lines.get(i + 1);
                if (!JOBPLOY_LABELS.contains(next)) {
                    map.put(line, next);
                }
            }
        }
        return map;
    }

    public static Map<String, String> extractSections(
            String pageText,
            List<String> headers
    ) {
        Map<String, String> out = new LinkedHashMap<>();
        if (pageText == null || pageText.isBlank()) return out;

        List<String> lines = Arrays.stream(pageText.split("\\R"))
                .map(String::trim)
                .toList();

        String current = null;
        StringBuilder buf = new StringBuilder();

        for (String line : lines) {
            if (headers.contains(line)) {
                if (current != null) {
                    out.put(current, buf.toString().trim());
                }
                current = line;
                buf = new StringBuilder();
                continue;
            }
            if (current != null) {
                buf.append(line).append("\n");
            }
        }

        if (current != null) {
            out.put(current, buf.toString().trim());
        }
        return out;
    }

    public static String extractCompanyName(String companySectionText) {
        if (companySectionText == null || companySectionText.isBlank()) return null;

        return Arrays.stream(companySectionText.split("\\R"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .findFirst()
                .orElse(null);
    }

    public static Long parseRecruitId(String url) {
        Matcher m = RECRUIT_ID_PATTERN.matcher(url);
        return m.find() ? Long.parseLong(m.group(1)) : null;
    }

}
