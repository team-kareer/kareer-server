package org.sopt.kareer.domain.roadmap.service.dto.response;

import java.util.List;

public record AiGuideData(
        String importance,
        List<String> guidelines,
        List<String> mistakes
) {}
