package org.sopt.kareer.domain.member.repository;

import org.sopt.kareer.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
