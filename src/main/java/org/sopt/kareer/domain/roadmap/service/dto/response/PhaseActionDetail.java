package org.sopt.kareer.domain.roadmap.service.dto.response;

import java.time.LocalDate;

public record PhaseActionDetail(
        Long id,
        String type,
        String title,
        LocalDate deadline
) {}
