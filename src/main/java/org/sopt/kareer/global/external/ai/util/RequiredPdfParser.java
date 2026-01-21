package org.sopt.kareer.global.external.ai.util;

import org.sopt.kareer.global.external.ai.dto.response.RequiredSection;
import org.sopt.kareer.global.external.ai.enums.RequiredCategory;
import org.sopt.kareer.global.external.ai.enums.RequiredDepth;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequiredPdfParser {
    private static final Pattern VISA_CASE = Pattern.compile(
            "\\[비자\\s*시뮬레이션\\s*Case\\s*(\\d+)]\\s*([^\\n\\r]+)\\s*\\(([^)]+)\\)\\s*(.*?)" +
            "(?=\\[비자\\s*시뮬레이션\\s*Case\\s*\\d+]|\\n\\s*D-2\\s*\\n|\\n\\s*D-10\\s*\\n|\\n\\s*E-7\\s*\\n|\\z)",
            Pattern.DOTALL
    );

    private static final Pattern CAREER_CASE = Pattern.compile(
            "\\[(.+?)\\s*커리어\\s*시뮬레이션\\s*Case\\s*(\\d+)]\\s*([^\\n\\r]+)\\s*\\(([^)]+)\\)\\s*(.*?)" +
            "(?=\\[.+?\\s*커리어\\s*시뮬레이션\\s*Case\\s*\\d+]|\\z)",
            Pattern.DOTALL
    );

    private static final Pattern DEPTH_1 =
            Pattern.compile("Depth\\s*1\\s*[-:]\\s*Action\\s*Required\\s*(.*?)\\s*(?=Depth\\s*2-1|\\z)", Pattern.DOTALL);

    private static final Pattern DEPTH_2_1 =
            Pattern.compile("Depth\\s*2-1\\s*[-:]\\s*AI\\s*Guide\\s*&\\s*Risk\\s*(.*?)\\s*(?=Depth\\s*2-2|\\z)", Pattern.DOTALL);

    private static final Pattern DEPTH_2_2 =
            Pattern.compile("Depth\\s*2-2\\s*[-:]\\s*To\\s*Do\\s*List\\s*(.*)\\s*", Pattern.DOTALL);

    public List<RequiredSection> parseVisa(String fullText) {
        Map<String, String> domainBlocks = splitByVisaDomain(fullText);

        List<RequiredSection> out = new ArrayList<>();
        for (var e : domainBlocks.entrySet()) {
            String domain = e.getKey();
            String block = e.getValue();

            Matcher m = VISA_CASE.matcher(block);
            while (m.find()) {
                int caseNo = Integer.parseInt(m.group(1));
                String title = safe(m.group(2)) + " (" + safe(m.group(3)) + ")";
                String body = safe(m.group(4));

                out.addAll(splitDepths(RequiredCategory.VISA, domain, caseNo, title, body));
            }
        }
        return out;
    }

    public List<RequiredSection> parseCareer(String fullText) {
        Map<String, String> domainBlocks = splitByCareerDomain(fullText);

        List<RequiredSection> out = new ArrayList<>();
        for (var e : domainBlocks.entrySet()) {
            String domain = e.getKey();
            String block = e.getValue();

            Matcher m = CAREER_CASE.matcher(block);
            while (m.find()) {
                int caseNo = Integer.parseInt(m.group(2));
                String title = safe(m.group(3)) + " (" + safe(m.group(4)) + ")";
                String body = safe(m.group(5));

                out.addAll(splitDepths(RequiredCategory.CAREER, domain, caseNo, title, body));
            }
        }
        return out;
    }

    private List<RequiredSection> splitDepths(RequiredCategory category, String domain, int caseNo, String title, String body) {
        String d1 = extract(DEPTH_1, body);
        String d21 = extract(DEPTH_2_1, body);
        String d22 = extract(DEPTH_2_2, body);

        List<RequiredSection> list = new ArrayList<>(3);
        if (!d1.isBlank()) list.add(section(category, domain, caseNo, title, RequiredDepth.D1, d1));
        if (!d21.isBlank()) list.add(section(category, domain, caseNo, title, RequiredDepth.D2_1, d21));
        if (!d22.isBlank()) list.add(section(category, domain, caseNo, title, RequiredDepth.D2_2, d22));
        return list;
    }

    private RequiredSection section(RequiredCategory category, String domain, int caseNo, String title, RequiredDepth depth, String text) {
        return RequiredSection.builder()
                .category(category)
                .domain(domain.trim())
                .caseNo(caseNo)
                .title(title.trim())
                .depth(depth)
                .text(normalize(text))
                .build();
    }

    private String extract(Pattern p, String body) {
        Matcher m = p.matcher(body);
        if (!m.find()) return "";
        return safe(m.group(1));
    }

    private String normalize(String s) {
        return s.replace("\r", "")
                .replaceAll("[ \\t]+", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private Map<String, String> splitByVisaDomain(String text) {
        return splitByHeaders(text, List.of("D-2", "D-10", "E-7"));
    }

    private Map<String, String> splitByCareerDomain(String text) {
        return splitByHeaders(text, List.of("Marketing", "Sales", "Planning & Strategy", "Production"));
    }

    private Map<String, String> splitByHeaders(String text, List<String> headers) {
        Map<String, String> out = new LinkedHashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            String h = headers.get(i);
            int start = indexOfHeader(text, h);
            if (start < 0) continue;

            int end = text.length();
            for (int j = i + 1; j < headers.size(); j++) {
                int next = indexOfHeader(text, headers.get(j));
                if (next > start) { end = next; break; }
            }

            out.put(h, text.substring(start, end));
        }
        return out;
    }

    private int indexOfHeader(String text, String header) {
        int idx = text.indexOf("\n" + header + "\n");
        if (idx >= 0) return idx;
        idx = text.indexOf(header + "\n");
        if (idx >= 0) return idx;
        return text.indexOf(header);
    }


}
