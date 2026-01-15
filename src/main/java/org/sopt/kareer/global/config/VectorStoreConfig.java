package org.sopt.kareer.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@RequiredArgsConstructor
public class VectorStoreConfig {

    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingModel embeddingModel;

    @Bean
    PgVectorStore documentVectorStore(){
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .vectorTableName("document_vectors")
                .initializeSchema(true)
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .build();
    }

    @Bean
    PgVectorStore jobPostingVectorStore(){
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .vectorTableName("job_posting_vectors")
                .initializeSchema(true)
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .build();
    }
}
