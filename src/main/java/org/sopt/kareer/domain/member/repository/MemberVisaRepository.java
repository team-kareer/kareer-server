package org.sopt.kareer.domain.member.repository;

import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.entity.enums.VisaStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberVisaRepository extends JpaRepository<MemberVisa, Long> {
    List<MemberVisa> findAllByMemberIdAndVisaStatus(Long memberId, VisaStatus visaStatus);
}
