package org.sopt.kareer.domain.term.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.member.dto.request.MemberTermsRequest;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.MemberTerm;
import org.sopt.kareer.domain.member.repository.MemberTermRepository;
import org.sopt.kareer.domain.member.service.MemberService;
import org.sopt.kareer.domain.term.entity.Term;
import org.sopt.kareer.domain.term.dto.response.TermsResponse;
import org.sopt.kareer.domain.term.exception.TermErrorCode;
import org.sopt.kareer.domain.term.exception.TermException;
import org.sopt.kareer.domain.term.repository.TermRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermService {

    private final TermRepository termRepository;
    private final MemberService memberService;
    private final MemberTermRepository memberTermRepository;

    public TermsResponse getTerms() {
        List<Term> terms = termRepository.findByActiveTrue();

        List<TermsResponse.TermResponse> termResponses = terms.stream()
                .sorted(Comparator.comparing(term -> term.getType().getOrder()))
                .map(TermsResponse.TermResponse::from)
                .toList();

        return TermsResponse.from(termResponses);
    }

    @Transactional
    public void agreeTerms(Long memberId, MemberTermsRequest request) {
        Member member = memberService.getById(memberId);
        List<MemberTermsRequest.TermAgreement> agreements = request.agreements();

        Map<Long, Boolean> agreementMap = agreements.stream()
                .collect(Collectors.toMap(
                        MemberTermsRequest.TermAgreement::termId,
                        MemberTermsRequest.TermAgreement::agreed,

                        // 중복된 term이 있는지 체크
                        (existing, replacement) -> {
                            throw new TermException(TermErrorCode.DUPLICATE_TERM);
                        }
                ));

        // 받아야하는 약관들 리스트
        List<Term> activeTerms = termRepository.findByActiveTrue();

        // 필요한 약관들에 대해 잘 요청됐는지 검증
        Set<Long> activeTermIds = activeTerms.stream()
                .map(Term::getId)
                .collect(Collectors.toSet());

        if (!activeTermIds.equals(agreementMap.keySet())) {
            throw new TermException(TermErrorCode.MISSING_TERM);
        }

        List<MemberTerm> memberTerms = activeTerms.stream()
                .map(term -> {
                    Boolean agreed = agreementMap.get(term.getId());

                    // 필수 약관인데 동의하지 않은 경우
                    if (term.isRequired() && !Boolean.TRUE.equals(agreed)) {
                        throw new TermException(TermErrorCode.REQUIRED_TERM_NOT_AGREED);
                    }

                    return MemberTerm.create(agreed, member, term);
                })
                .toList();

        memberTermRepository.saveAll(memberTerms);
    }
}
