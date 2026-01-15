package org.sopt.kareer.domain.phase.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PhaseStatus {

    PREVIOUS("Previous"),
    PAST("Past"),
    CURRENT("Current", "Remained works"),
    NEXT("Next", "Scheduled works"),
    FUTURE("Future", "Scheduled works");


    private final String displayName;
    private final String workStatus;

    PhaseStatus(String displayName) {
        this.displayName = displayName;
        this.workStatus = null; // 남은 actions 수에 따라 동적으로 결정되므로 null
    }

    public static String determineWorkStatus(PhaseStatus status, Long workCount) {
        if (status == PhaseStatus.PREVIOUS || status == PhaseStatus.PAST) {
            return workCount == 0 ? "All completed" : "Incomplete works";
        }
        return status.getWorkStatus();
    }
}
