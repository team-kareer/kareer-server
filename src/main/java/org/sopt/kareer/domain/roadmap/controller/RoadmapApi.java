package org.sopt.kareer.domain.roadmap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapTestResponse;
import org.sopt.kareer.global.annotation.CustomExceptionDescription;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;

import static org.sopt.kareer.global.config.swagger.SwaggerResponseDescription.CREATE_ROADMAP;

@Tag(name = "Roadmap API", description = "Roadmap 관련 API")
public interface RoadmapApi {

    @Operation(summary = "AI 로드맵 생성", description = "사용자가 온보딩에 입력한 정보를 통해 로드맵을 생성합니다.")
    @CustomExceptionDescription(CREATE_ROADMAP)
    @PostMapping
    ResponseEntity<BaseResponse<Void>> generateRoadmap(@AuthenticationPrincipal Long memberId);

    @Operation(summary = "AI 로드맵 생성 테스트용 (Server Only)", description = "사용자가 온보딩에 입력한 정보를 통해 로드맵을 생성합니다.")
    @CustomExceptionDescription(CREATE_ROADMAP)
    @PostMapping("/test")
    ResponseEntity<BaseResponse<RoadmapTestResponse>> generateRoadmapForTest(@AuthenticationPrincipal Long memberId);
}
