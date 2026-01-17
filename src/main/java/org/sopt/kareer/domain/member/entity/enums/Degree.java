package org.sopt.kareer.domain.member.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Degree {
    DOMESTIC_ASSOCIATE("South Korea•Associate"),
    DOMESTIC_BACHELORS("South Korea•Bachelor"),
    DOMESTIC_MASTERS("South Korea•Master"),
    DOMESTIC_DOCTORATE("South Korea•Doctorate"),
    OVERSEAS_BACHELORS("Outside Korea•Bachelor"),
    OVERSEAS_MASTERS("Outside Korea•Master"),
    OVERSEAS_DOCTORATE("Outside Korea•Doctorate")
    ;

    private final String description;

}
