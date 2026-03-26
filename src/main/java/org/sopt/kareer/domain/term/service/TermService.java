package org.sopt.kareer.domain.term.service;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.term.entity.Term;
import org.sopt.kareer.domain.term.dto.response.TermsResponse;
import org.sopt.kareer.domain.term.repository.TermRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermService {

    private final TermRepository termRepository;

    public TermsResponse getTerms() {
        List<Term> terms = termRepository.findByActiveTrue();

        List<TermsResponse.TermResponse> termResponses = terms.stream()
                .sorted(Comparator.comparing(term -> term.getType().getOrder()))
                .map(TermsResponse.TermResponse::from)
                .toList();

        return TermsResponse.from(termResponses);
    }
}
