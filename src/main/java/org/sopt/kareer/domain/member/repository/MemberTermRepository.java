package org.sopt.kareer.domain.member.repository;

import org.sopt.kareer.domain.member.entity.MemberTerm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTermRepository extends JpaRepository<MemberTerm, Long> {

    boolean existsByMemberId(Long memberId);
}
