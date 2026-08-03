package org.sopt.kareer.global.external.ai.evaluation;

public record PipelineMetrics(
        String pipelineName,
        Double contextPrecision,
        Double contextRecall,
        Double faithfulness,
        Double answerRelevancy,
        int caseCount
) {
}
