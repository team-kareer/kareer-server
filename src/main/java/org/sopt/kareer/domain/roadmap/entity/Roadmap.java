package org.sopt.kareer.domain.roadmap.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.domain.roadmap.entity.enums.RoadmapActiveStatus;
import org.sopt.kareer.global.entity.BaseEntity;

@Entity
@Table(name = "roadmaps")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Roadmap extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roadmap_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoadmapActiveStatus status;

    public static Roadmap create(Member member) {
        return Roadmap.builder()
                .member(member)
                .status(RoadmapActiveStatus.ACTIVE)
                .build();
    }

    public void deactivate() {
        this.status = RoadmapActiveStatus.INACTIVE;
    }
}
