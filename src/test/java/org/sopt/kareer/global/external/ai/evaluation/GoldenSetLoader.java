package org.sopt.kareer.global.external.ai.evaluation;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class GoldenSetLoader {

    private static final String GOLDEN_SET_CLASSPATH = "ragas/golden-set.json";

    private final ObjectMapper objectMapper;

    public GoldenSetLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<GoldenCase> load() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(GOLDEN_SET_CLASSPATH)) {
            if (is == null) {
                throw new IllegalStateException("Golden set resource not found on classpath: " + GOLDEN_SET_CLASSPATH);
            }
            return objectMapper.readValue(is, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, GoldenCase.class));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load golden set from " + GOLDEN_SET_CLASSPATH, e);
        }
    }
}
