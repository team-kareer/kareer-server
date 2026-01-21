package org.sopt.kareer.domain.roadmap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.exception.MemberErrorCode;
import org.sopt.kareer.domain.member.exception.MemberException;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoadmapAsyncService {

    private final RoadMapService roadMapService;
    private final ExecutorService executorService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Transactional
    public void generateRoadmapAsync(Long memberId){

        Member member = memberService.getById(memberId);

        member.assertCanStartRoadmap();

        int updated = memberRepository.tryMarkRoadmapInProgress(memberId);
        if (updated == 0) {
            throw new MemberException(MemberErrorCode.ROADMAP_IN_PROGRESS);
        }

        executorService.submit(() -> {
            try {
                roadMapService.createRoadmap(memberId);
            } catch (Exception e) {
                log.error("로드맵 생성 실패, memberId = {}", memberId, e);
                roadMapService.markFailed(memberId);
            }
        });

    }
}
