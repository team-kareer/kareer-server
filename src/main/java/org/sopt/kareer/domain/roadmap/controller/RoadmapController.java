package org.sopt.kareer.domain.roadmap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapTestResponse;
import org.sopt.kareer.domain.roadmap.service.RoadMapService;
import org.sopt.kareer.domain.roadmap.service.RoadmapTranslationService;
import org.sopt.kareer.global.annotation.CustomExceptionDescription;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.sopt.kareer.global.config.swagger.SwaggerResponseDescription.CREATE_ROADMAP;

@Tag(name = "Roadmap API", description = "Roadmap 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roadmap")
public class RoadmapController {

    private final RoadMapService roadMapService;
    private final RoadmapTranslationService roadmapTranslationService;

    @Operation(summary = "AI 로드맵 생성", description = "사용자가 온보딩에 입력한 정보를 통해 로드맵을 생성합니다.")
    @CustomExceptionDescription(CREATE_ROADMAP)
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> generateRoadmap(
            @AuthenticationPrincipal Long memberId) {

        var target = roadMapService.createRoadmap(memberId);
        roadmapTranslationService.translateAllLanguages(target);

        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok("AI 로드맵 생성에 성공하였습니다."));
    }

    @Operation(summary = "AI 로드맵 생성 테스트용 (Server Only)", description = "사용자가 온보딩에 입력한 정보를 통해 로드맵을 생성합니다.")
    @CustomExceptionDescription(CREATE_ROADMAP)
    @PostMapping("/test")
    public ResponseEntity<BaseResponse<RoadmapTestResponse>> generateRoadmapForTest(
            @AuthenticationPrincipal Long memberId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(roadMapService.createRoadmapTest(memberId), "AI 로드맵 생성에 성공하였습니다."));
    }
}
