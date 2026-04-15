package org.sopt.kareer.domain.member.fixture;

import org.sopt.kareer.domain.member.dto.request.MemberOnboardV2Request;
import org.sopt.kareer.domain.member.entity.enums.Degree;
import org.sopt.kareer.domain.member.entity.enums.EnglishLevel;
import org.sopt.kareer.domain.member.entity.enums.LanguageLevel;
import org.sopt.kareer.domain.member.entity.enums.VisaType;

import java.time.LocalDate;
import java.util.List;

public class MemberOnboardRequestFixture {

    public static MemberOnboardV2Request create() {
        return new MemberOnboardV2Request(
                "test-user",
                LocalDate.of(2000, 4, 1),
                "konkuk-university",
                "united-states",
                LanguageLevel.LEVEL_4,
                EnglishLevel.ADVANCED,
                Degree.OVERSEAS_BACHELORS,
                VisaType.D2,
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2022, 3, 1),
                LocalDate.of(2026, 5, 1),
                "computer-science",
                "applied-mathematics",
                List.of("it-development", "data-science"),
                List.of(),
                "Backend Engineer",
                "Java, Spring",
                "해외 경험을 쌓고 싶어요"
        );
    }
}
