package org.sopt.kareer.global.external.ai.evaluation;

public record PhaseResult(
        int sequence,
        String goal,
        double faithfulness,
        double answerRelevancy
) {
}
