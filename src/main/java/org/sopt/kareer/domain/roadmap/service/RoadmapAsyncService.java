package org.sopt.kareer.domain.roadmap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoadmapAsyncService {

    private final RoadMapService roadMapService;
    private final ExecutorService executorService;

    public void generateRoadmapAsync(Long memberId){
        executorService.submit(() -> {
            try{
                roadMapService.createRoadmap(memberId);
            } catch (Exception e){
                log.error("로드맵 생성 실패, memberId = {}", memberId, e);
            }

        });
    }
}
