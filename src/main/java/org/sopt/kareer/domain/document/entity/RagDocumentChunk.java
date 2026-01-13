package org.sopt.kareer.domain.document.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sopt.kareer.domain.document.util.FloatArrayToVectorStringConverter;
import org.sopt.kareer.global.entity.BaseEntity;

import java.util.UUID;

@Entity
@Table(
        name = "rag_document_chunks",
        uniqueConstraints = @UniqueConstraint(name = "uq_doc_chunk", columnNames = {"document_id", "chunk_index"}),
        indexes = {
                @Index(name = "idx_chunks_doc", columnList = "document_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RagDocumentChunk {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID chunkId;

    @Column(columnDefinition = "uuid", nullable = false)
    private UUID documentId;

    @Column(nullable = false)
    private int chunkIndex;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(nullable = false, columnDefinition = "jsonb")
    private String metadataJson;

    @Convert(converter = FloatArrayToVectorStringConverter.class)
    @Column(nullable = false, columnDefinition = "vector(1536)")
    private float[] embedding;

}
