package org.sopt.kareer.domain.roadmap.service.dto.response;

import java.time.LocalDate;
import org.sopt.kareer.domain.roadmap.entity.enums.ActionItemType;

public record ActionItemDetail(
        Long id,
        ActionItemType type,
        String title,
        LocalDate deadline,
        boolean completed
) {}
