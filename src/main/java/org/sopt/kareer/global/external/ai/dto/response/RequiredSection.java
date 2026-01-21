package org.sopt.kareer.global.external.ai.dto.response;

import lombok.Builder;
import org.sopt.kareer.global.external.ai.enums.RequiredCategory;
import org.sopt.kareer.global.external.ai.enums.RequiredDepth;

@Builder
public record RequiredSection(
        RequiredCategory category,
        String domain,
        int caseNo,
        String title,
        RequiredDepth depth,
        String text
) {
}
