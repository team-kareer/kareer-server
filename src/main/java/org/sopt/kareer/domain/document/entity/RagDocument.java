package org.sopt.kareer.domain.document.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sopt.kareer.global.entity.BaseEntity;

import java.util.UUID;

@Entity
@Table(name = "rag_documents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RagDocument extends BaseEntity {

    @Id
    @Column(name = "document_id", columnDefinition = "uuid")
    private UUID documentId;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "upload_time", nullable = false)
    private long uploadTime;


}
