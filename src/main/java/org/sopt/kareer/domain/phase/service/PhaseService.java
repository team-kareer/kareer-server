package org.sopt.kareer.domain.phase.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.exception.MemberErrorCode;
import org.sopt.kareer.domain.member.exception.MemberException;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.domain.phase.dto.response.PhaseResponse;
import org.sopt.kareer.domain.phase.dto.response.PhaseListResponse;
import org.sopt.kareer.domain.phase.repository.PhaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhaseService {

    private final MemberRepository memberRepository;
    private final PhaseRepository phaseRepository;

    public PhaseListResponse getPhases(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        List<PhaseResponse> responses= phaseRepository.findPhases(memberId);

        return PhaseListResponse.from(responses);
    }
}
