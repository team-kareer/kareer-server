package org.sopt.kareer.domain.document.repository;

import org.sopt.kareer.domain.document.entity.RagDocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface RagDocumentChunkRepository extends JpaRepository<RagDocumentChunk, UUID> {

    @Modifying
    @Transactional
    @Query(value = """
    INSERT INTO rag_document_chunks
        (document_chunk_id, document_id, chunk_index, content, metadata, embedding)
    VALUES
        (:chunkId, :documentId, :chunkIndex, :content, CAST(:metadataJson AS jsonb), CAST(:embedding AS vector))
    """, nativeQuery = true)
    void insertChunk(
            @Param("chunkId") UUID chunkId,
            @Param("documentId") UUID documentId,
            @Param("chunkIndex") int chunkIndex,
            @Param("content") String content,
            @Param("metadataJson") String metadataJson,
            @Param("embedding") String embeddingVectorLiteral
    );
}
