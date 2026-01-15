package org.sopt.kareer.domain.roadmap.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sopt.kareer.domain.roadmap.exception.RoadMapException;

import java.util.Arrays;

import static org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode.PHASE_STATUS_BLANK;
import static org.sopt.kareer.domain.roadmap.exception.RoadmapErrorCode.PHASE_STATUS_INVALID;

@Getter
@AllArgsConstructor
public enum PhaseStatus {
    PREVIOUS("Previous"),
    PAST("Past"),
    CURRENT("Current", "Remained works"),
    NEXT("Next", "Scheduled works"),
    FUTURE("Future", "Scheduled works");
    ;

    private final String displayName;
    private final String workStatus;

    PhaseStatus(String displayName) {
        this.displayName = displayName;
        this.workStatus = null; // 남은 actions 수에 따라 동적으로 결정되므로 null
    }

    public static PhaseStatus from(String value){
        if (value == null || value.isBlank()){
            throw new RoadMapException(PHASE_STATUS_BLANK);
        }

        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new RoadMapException(PHASE_STATUS_INVALID));
    }


    public static String determineWorkStatus(PhaseStatus status, Long workCount) {
        if (status == PhaseStatus.PREVIOUS || status == PhaseStatus.PAST) {
            return (workCount == null || workCount == 0) ? "All completed" : "Incomplete works";
        }
        return status.getWorkStatus();
    }
}
