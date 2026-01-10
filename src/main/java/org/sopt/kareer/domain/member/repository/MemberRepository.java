package org.sopt.kareer.domain.member.repository;

import java.util.Optional;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByProviderAndProviderId(OAuthProvider provider, String providerId);
}
