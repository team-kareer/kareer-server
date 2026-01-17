package org.sopt.kareer.domain.jobposting.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sopt.kareer.domain.member.entity.Member;
import org.sopt.kareer.global.entity.BaseEntity;

@Entity
@Table(
        name = "job_posting_bookmark",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_member_job_posting",
                        columnNames = {"member_id", "job_posting_id"}
                )
        }
)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobPostingBookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_posting_bookmark_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @Builder
    private JobPostingBookmark(Member member, JobPosting jobPosting) {
        this.member = member;
        this.jobPosting = jobPosting;
    }

    public static JobPostingBookmark create(Member member, JobPosting jobPosting) {
        return JobPostingBookmark.builder()
                .member(member)
                .jobPosting(jobPosting)
                .build();
    }
}
