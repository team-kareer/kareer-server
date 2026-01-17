package org.sopt.kareer.domain.jobposting.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.jobposting.constant.JobPostingCrawlConstants;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.sopt.kareer.domain.jobposting.constant.JobPostingCrawlConstants.*;

class CrawledTextUtilTest {

    @DisplayName("공고 사이트의 Deadline 관련 텍스트로부터 Deadline을 추출한다.")
    @Test
    void extractDeadline(){
       //given
        String deadlineText = "Until deadline : : 26. 02. 03 (Tuesday)";
        LocalDate deadline = LocalDate.of(2026,2,3);

       //when
        LocalDate parsedDeadline = CrawledTextUtil.extractDeadline(deadlineText);

        //then
        assertThat(parsedDeadline).isEqualTo(deadline);
    }

    @DisplayName("공고 사이트의 Job Summary 부분의 텍스트를 추출한다.")
    @Test
    void extractJobSummaryFields(){
       //given
        String pageText = """
                Arrangement
                Intern, Regular work

                Visa
                D-2, D-10

                Language
                TOPIK 4

                Site
                Chungnam Asan-si
                """;

       //when
        Map<String, String> result = CrawledTextUtil.extractJobSummaryFields(pageText);

        //then
        assertThat(result).containsEntry(LABEL_ARRANGEMENT, "Intern, Regular work");
        assertThat(result).containsEntry(LABEL_VISA, "D-2, D-10");
        assertThat(result).containsEntry(LABEL_LANGUAGE, "TOPIK 4");
        assertThat(result).containsEntry(LABEL_SITE, "Chungnam Asan-si");
    }

    @DisplayName("섹션 별로 구성되어 있는 본문에서 헤더별 본문이 Map으로 분리된다. 헤더에 포함되지 않는 본문은 무시된다.")
    @Test
    void extractSections(){

        //given
        String pageText = """
                Intro line (ignored)

                Detailed work requirements
                line1
                line2

                Recruitment conditions
                cond1
                cond2

                Company
                CompanyName
                CEO
                """;

        // When
        Map<String, String> result = CrawledTextUtil.extractSections(pageText, SECTION_HEADERS);

        // Then
        assertThat(result).containsEntry("Detailed work requirements", "line1\nline2");
        assertThat(result).containsEntry("Recruitment conditions", "cond1\ncond2");
        assertThat(result).containsEntry("Company", "CompanyName\nCEO");
        assertThat(result).doesNotContainKey("Intro line (ignored)");
    }

    @DisplayName("extractSections 메서드에 의해 반환된 맵에서 Company 정보를 추출할 수 있다.")
    @Test
    void extractCompanyName(){
       //given
       String companySectionText = "CompanyName\nCEO";

       //when
        String companyName = CrawledTextUtil.extractCompanyName(companySectionText);

        //then
        assertThat(companyName).isEqualTo("CompanyName");
    }

    @DisplayName("채용 공고 url로부터 recruitId를 추출할 수 있다.")
    @Test
    void parseRecruitId(){
       //given
       String url = "https://www.jobploy.kr/en/recruit/RC0000117839";

       //when
        Long recruitId = CrawledTextUtil.parseRecruitId(url);

        //then
        assertThat(recruitId).isEqualTo(117839);
    }
}