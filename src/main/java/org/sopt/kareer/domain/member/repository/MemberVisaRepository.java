package org.sopt.kareer.domain.member.repository;

import org.sopt.kareer.domain.member.entity.MemberVisa;
import org.sopt.kareer.domain.member.entity.enums.VisaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberVisaRepository extends JpaRepository<MemberVisa, Long> {
    List<MemberVisa> findAllByMemberIdAndVisaStatus(Long memberId, VisaStatus visaStatus);

    @Query("""
        select mv from MemberVisa mv where mv.member.id = :memberId and mv.visaStatus = 'ACTIVE'
        """)
    Optional<MemberVisa> findActiveByMemberId(Long memberId);
}
