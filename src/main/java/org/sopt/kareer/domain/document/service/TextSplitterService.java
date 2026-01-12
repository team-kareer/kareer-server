package org.sopt.kareer.domain.document.service;

import lombok.Getter;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TextSplitterService {

    TokenTextSplitter splitter = TokenTextSplitter.builder()
            .withChunkSize(512)
            .withMinChunkSizeChars(350)
            .withMinChunkLengthToEmbed(5)
            .withMaxNumChunks(10000)
            .withKeepSeparator(true)
            .build();

    public List<String> split(String text) {
        List<Document> docs = splitter.split(List.of(new Document(text)));
        return docs.stream()
                .map(Document::getText)
                .toList();
    }
}
