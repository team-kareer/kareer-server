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
    @Column(columnDefinition = "uuid")
    private UUID documentId;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private long uploadTime;

    @Column(nullable = false, unique = true)
    private String fileHash;

}
