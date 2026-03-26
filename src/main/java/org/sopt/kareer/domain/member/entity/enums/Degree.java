package org.sopt.kareer.domain.member.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Degree {
    DOMESTIC_ASSOCIATE("south-korea-associate"),
    DOMESTIC_BACHELORS("south-korea-bachelor"),
    DOMESTIC_MASTERS("south-korea-master"),
    DOMESTIC_DOCTORATE("south-korea-doctorate"),
    OVERSEAS_BACHELORS("outside-korea-bachelor"),
    OVERSEAS_MASTERS("outside-korea-master"),
    OVERSEAS_DOCTORATE("outside-korea-doctorate")
    ;

    private final String description;

}
