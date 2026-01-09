package org.sopt.kareer.domain.jobposting.constant;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class JobPostingCrawlConstants {

    // URL / PATH
    public static final String BASE_URL =
            "https://www.jobploy.kr/en/recruit/list/depopulation";

    public static final String RECRUIT_DETAIL_URL_PATH =
            "/en/recruit/RC";


    // Regex / Parsing Patterns
    public static final Pattern RECRUIT_ID_PATTERN =
            Pattern.compile(".*/RC0*(\\d+)$");

    public static final Pattern DEADLINE_DATE_PATTERN =
            Pattern.compile("(\\d{2})\\.\\s*(\\d{2})\\.\\s*(\\d{2})");


    // Text Prefix / Labels
    public static final String DEADLINE_PREFIX =
            "Until deadline";

    public static final String LABEL_ARRANGEMENT = "Arrangement";
    public static final String LABEL_VISA        = "Visa";
    public static final String LABEL_LANGUAGE    = "Language";
    public static final String LABEL_SITE        = "Site";
    public static final String LABEL_COMPANY     = "Company";

    public static final Set<String> JOBPLOY_LABELS = Set.of(
            LABEL_ARRANGEMENT,
            LABEL_VISA,
            LABEL_LANGUAGE,
            LABEL_SITE,
            LABEL_COMPANY
    );


    // Section Headers
    public static final List<String> SECTION_HEADERS = List.of(
            "Detailed work requirements",
            "Recruitment conditions",
            "Working conditions",
            "Company"
    );


    // CSS Selectors
    public static final String SLIDER_IMAGE_CSS =
            "#recruitSlider a[href]";


    // JavaScript Scripts
    public static final String JS_COLLECT_ALL_HREFS = """
        return Array.from(document.querySelectorAll('a[href]'))
          .map(a => a.href)
          .filter(Boolean);
        """;

    public static final String JS_DOCUMENT_READY_STATE =
            "return document.readyState";

    // Integer Constants

    public static int MAX_LIST_PAGES_ALL = 500;
    public static final int MAX_LIST_PAGES = 50;


}
