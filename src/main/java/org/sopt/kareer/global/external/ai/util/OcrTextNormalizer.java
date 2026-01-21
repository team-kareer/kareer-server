package org.sopt.kareer.global.external.ai.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public final class OcrTextNormalizer {
    private static final Pattern PUA = Pattern.compile("[\\uE000-\\uF8FF]");

    private static final Pattern DASHES = Pattern.compile("[‐-‒–—―]");

    private static final Pattern CONTROL = Pattern.compile("[\\p{Cc}\\p{Cf}]");
    private static final Pattern MULTI_SPACE = Pattern.compile("[ \\t\\x0B\\f\\r]+");
    private static final Pattern MULTI_NEWLINE = Pattern.compile("\\n{3,}");

    private OcrTextNormalizer() {}

    public static String normalize(String raw) {
        if (raw == null) return "";

        String s = Normalizer.normalize(raw, Normalizer.Form.NFKC);

        s = PUA.matcher(s).replaceAll("-");

        s = DASHES.matcher(s).replaceAll("-");

        s = CONTROL.matcher(s).replaceAll("");

        s = MULTI_SPACE.matcher(s).replaceAll(" ");
        s = s.replace("\r\n", "\n").replace("\r", "\n");
        s = MULTI_NEWLINE.matcher(s).replaceAll("\n\n");

        return s.trim();
    }

    public static String forceNewlines(String s) {
        if (s == null) return "";

        String t = s.replace("\r", "");

        t = t.replaceAll("[ \\t]+", " ");

        t = t.replaceAll("\\s*(시뮬레이션\\s*카드\\s*-\\s*(비자|커리어)\\s*\\d*)\\s*", "\n$1\n");

        t = t.replaceAll("\\s*(D-2|D-10|E-7)\\s*", "\n$1\n");

        t = t.replaceAll("\\s*(Marketing)\\s*", "\n$1\n");
        t = t.replaceAll("\\s*(Sales)\\s*", "\n$1\n");
        t = t.replaceAll("\\s*(Planning\\s*&\\s*Strategy|Planning\\s*and\\s*Strategy)\\s*", "\nPlanning & Strategy\n");
        t = t.replaceAll("\\s*(Production)\\s*", "\n$1\n");

        t = t.replaceAll("\\s*\\[(비자\\s*시뮬레이션\\s*Case\\s*\\d+)\\]\\s*", "\n[$1] ");


        t = t.replaceAll("\\s*\\[([^\\]]+?\\s*커리어\\s*시뮬레이션\\s*Case\\s*\\d+)\\]\\s*", "\n[$1] ");


        t = t.replaceAll("\\s*(Depth\\s*1\\s*[-:]\\s*Action\\s*Required)\\s*", "\n$1\n");
        t = t.replaceAll("\\s*(Depth\\s*2-1\\s*[-:]\\s*AI\\s*Guide\\s*&\\s*Risk)\\s*", "\n$1\n");
        t = t.replaceAll("\\s*(Depth\\s*2-2\\s*[-:]\\s*To\\s*Do\\s*List)\\s*", "\n$1\n");

        t = t.replaceAll("\\n{3,}", "\n\n");
        return t.trim();
    }
}
