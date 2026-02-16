package org.sopt.kareer.domain.member.fixture;

import java.time.LocalDate;
import org.sopt.kareer.domain.member.dto.request.MemberOnboardRequest;
import org.sopt.kareer.domain.member.entity.enums.Country;
import org.sopt.kareer.domain.member.entity.enums.Degree;
import org.sopt.kareer.domain.member.entity.enums.LanguageLevel;
import org.sopt.kareer.domain.member.entity.enums.VisaType;

public class MemberOnboardRequestFixture {

    public static MemberOnboardRequest create() {
        return new MemberOnboardRequest(
                "test-user",
                LocalDate.of(2000, 4, 1),
                Country.UNITED_STATES,
                LanguageLevel.LEVEL_4,
                Degree.OVERSEAS_BACHELORS,
                VisaType.D10,
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2026, 5, 1),
                70,
                "Computer Science",
                "Applied Mathematics",
                "Backend Engineer",
                "Java, Spring",
                "해외 경험을 쌓고 싶어요"
        );
    }
}
