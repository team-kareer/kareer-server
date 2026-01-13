package org.sopt.kareer.domain.document.repository;

import org.sopt.kareer.domain.document.entity.RagDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RagDocumentRepository extends JpaRepository<RagDocument, UUID> {
    boolean existsByFileHash(String fileHash);
}
