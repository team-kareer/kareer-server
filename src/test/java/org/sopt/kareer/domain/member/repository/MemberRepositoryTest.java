package org.sopt.kareer.domain.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.member.entity.enums.RoadmapStatus;
import org.sopt.kareer.domain.member.fixture.MemberFixture;
import org.sopt.kareer.global.config.QuerydslConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@Import(QuerydslConfig.class)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("provider와 providerId 조합으로 회원을 조회할 수 있다.")
    @Test
    void findByProviderAndProviderId() {
        Member member = memberRepository.save(MemberFixture.getMember("provider-1"));

        Optional<Member> found = memberRepository.findByProviderAndProviderId(
                member.getProvider(),
                member.getProviderId()
        );

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(member.getId());
    }

    @DisplayName("로드맵 상태가 NOT_STARTED면 IN_PROGRESS로 전환된다.")
    @Test
    void tryMarkRoadmapInProgress() {
        Member member = memberRepository.save(MemberFixture.getMember("provider-2"));

        int updatedCount = memberRepository.tryMarkRoadmapInProgress(member.getId());

        assertThat(updatedCount).isEqualTo(1);
        Member updatedMember = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(updatedMember.getRoadmapStatus()).isEqualTo(RoadmapStatus.IN_PROGRESS);
    }

    @DisplayName("이미 로드맵이 완료된 회원은 상태가 변경되지 않는다.")
    @Test
    void tryMarkRoadmapInProgressWithDoneStatus() {
        Member member = memberRepository.save(MemberFixture.getMember("provider-3"));
        member.markRoadmapDone();
        memberRepository.save(member);

        int updatedCount = memberRepository.tryMarkRoadmapInProgress(member.getId());

        assertThat(updatedCount).isZero();
        Member foundMember = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(foundMember.getRoadmapStatus()).isEqualTo(RoadmapStatus.DONE);
    }
}
