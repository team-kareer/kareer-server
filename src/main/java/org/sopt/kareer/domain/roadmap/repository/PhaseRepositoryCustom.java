package org.sopt.kareer.domain.roadmap.repository;

import org.sopt.kareer.domain.roadmap.dto.response.PhaseResponse;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhaseRepositoryCustom {

    List<PhaseResponse> findPhases(@Param("memberId") Long memberId);
}
