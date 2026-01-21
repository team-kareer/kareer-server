package org.sopt.kareer.global.external.ai.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.rag.roadmap")
public record RoadmapRagProperties(
        int policyTopK,
        int careerActionTopK,
        int careerGuideTopK,
        int careerTodoTopK,
        int candidatePoolTopK
) {}
