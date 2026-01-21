package org.sopt.kareer.domain.member.repository;

import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.enums.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByProviderAndProviderId(OAuthProvider provider, String providerId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Member m
        set m.roadmapStatus = 'IN_PROGRESS'
        where m.id = :memberId
          and m.roadmapStatus in ('NOT_STARTED','FAILED')
    """)
    int tryMarkRoadmapInProgress(@Param("memberId") Long memberId);
}
