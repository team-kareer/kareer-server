package org.sopt.kareer.domain.roadmap.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.entity.QActionItem;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseResponse;
import org.sopt.kareer.domain.roadmap.dto.response.QPhaseResponse;
import org.sopt.kareer.domain.roadmap.entity.QPhase;
import org.sopt.kareer.domain.roadmap.entity.QPhaseAction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PhaseRepositoryCustomImpl implements PhaseRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public List<PhaseResponse> findPhases(Long memberId) {
        QPhase phase = QPhase.phase;
        QPhaseAction phaseAction = QPhaseAction.phaseAction;
        QActionItem actionItem = QActionItem.actionItem;

        return query
                .select(new QPhaseResponse(
                        phase.id,
                        phase.status,
                        phase.sequence,
                        phase.goal,
                        phase.description,


                        // 미완료 PhaseAction 개수
                        JPAExpressions
                                .select(phaseAction.count())
                                .from(phaseAction)
                                .where(
                                        phaseAction.phase.id.eq(phase.id),

                                        // 1. ActionItem이 0개이거나 미완료 action item이 1개 이상
                                        JPAExpressions
                                                .select(actionItem.count())
                                                .from(actionItem)
                                                .where(actionItem.phaseAction.eq(phaseAction))
                                                .eq(0L)
                                                .or(
                                                        JPAExpressions
                                                                .select(actionItem.count())
                                                                .from(actionItem)
                                                                .where(
                                                                        actionItem.phaseAction.eq(phaseAction),
                                                                        actionItem.completed.eq(false)
                                                                )
                                                                .gt(0L)
                                                )
                                ),
                        phase.startDate,
                        phase.endDate
                ))
                .from(phase)
                .where(phase.member.id.eq(memberId))
                .orderBy(phase.sequence.asc())
                .fetch();
    }
}
