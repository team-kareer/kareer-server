package org.sopt.kareer.domain.roadmap.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapTestResponse;
import org.sopt.kareer.domain.roadmap.facade.RoadmapGenerateFacade;
import org.sopt.kareer.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roadmap")
public class RoadmapController implements RoadmapApi {

    private final RoadmapGenerateFacade roadmapGenerateFacade;

    @Override
    public ResponseEntity<BaseResponse<Void>> generateRoadmap(
            @AuthenticationPrincipal Long memberId) {

        roadmapGenerateFacade.generateRoadmap(memberId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok("AI 로드맵 생성에 성공하였습니다."));
    }

    @Override
    public ResponseEntity<BaseResponse<RoadmapTestResponse>> generateRoadmapForTest(
            @AuthenticationPrincipal Long memberId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(roadmapGenerateFacade.generateRoadmapForTest(memberId), "AI 로드맵 생성에 성공하였습니다."));
    }
}
