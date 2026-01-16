package org.sopt.kareer.domain.roadmap.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.domain.roadmap.dto.response.QRoadmapPhaseDetailResponse_ActionGroupResponse_ActionResponse;
import org.sopt.kareer.domain.roadmap.dto.response.RoadmapPhaseDetailResponse;
import org.sopt.kareer.domain.roadmap.entity.QActionItem;
import org.sopt.kareer.domain.roadmap.dto.response.PhaseResponse;
import org.sopt.kareer.domain.roadmap.dto.response.QPhaseResponse;
import org.sopt.kareer.domain.roadmap.entity.QPhase;
import org.sopt.kareer.domain.roadmap.entity.QPhaseAction;
import org.sopt.kareer.domain.roadmap.entity.enums.PhaseActionType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

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

    @Override
    public Map<String, List<RoadmapPhaseDetailResponse.ActionGroupResponse.ActionResponse>> getRoadmapPhaseDetail(Long phaseId) {
        QPhaseAction phaseAction = QPhaseAction.phaseAction;
        QActionItem item = QActionItem.actionItem;

        // 해당 PhaseAction이 done인지 판단 (item이 1개 이상이면서 모두 완료된 경우 done)
        BooleanExpression allDone =
                JPAExpressions
                        .select(item.count())
                        .from(item)
                        .where(
                                item.phaseAction.id.eq(phaseAction.id),
                                item.completed.isFalse()
                        )
                        .eq(0L)
                        .and(
                                JPAExpressions
                                        .select(item.count())
                                        .from(item)
                                        .where(item.phaseAction.id.eq(phaseAction.id))
                                        .gt(0L)
                        );
        ;

        // Visa, Career, Done 라벨링
        Expression<String> typeLabel = new CaseBuilder()
                .when(allDone).then("Done")
                .when(phaseAction.type.eq(PhaseActionType.VISA)).then("Visa")
                .when(phaseAction.type.eq(PhaseActionType.CAREER)).then("Career")
                .otherwise(phaseAction.type.stringValue());

        // Visa, Career, Done로 그룹핑 후 리스트 반환
        return query
                .from(phaseAction)
                .where(phaseAction.phase.id.eq(phaseId))
                .orderBy(phaseAction.deadline.asc(), phaseAction.title.asc()) // deadline 가까운 순으로 정렬, deadline이 같다면
                .transform(
                        groupBy(typeLabel).as(
                                list(new QRoadmapPhaseDetailResponse_ActionGroupResponse_ActionResponse(
                                        phaseAction.title,
                                        phaseAction.description,
                                        phaseAction.deadline
                                ))
                        )
                );
    }
}
