package org.sopt.kareer.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.repository.MemberRepository;
import org.sopt.kareer.global.exception.customexception.NotFoundException;
import org.sopt.kareer.global.exception.errorcode.MemberErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public Member getById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
