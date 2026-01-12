package org.sopt.kareer.domain.document.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Getter
@Service
public class EmbeddingService {

    private final OpenAiEmbeddingModel embeddingModel;

    public EmbeddingService(
            OpenAiApi openAiApi,
            @Value("${spring.ai.openai.embedding.options.model}") String embeddingModelName
    ) {
        this.embeddingModel = new OpenAiEmbeddingModel(
                openAiApi,
                MetadataMode.EMBED,
                OpenAiEmbeddingOptions.builder()
                        .model(embeddingModelName)
                        .build(),
                RetryUtils.DEFAULT_RETRY_TEMPLATE
        );
    }
}
