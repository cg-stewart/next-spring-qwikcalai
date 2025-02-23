package com.qwikcalai.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "image_uploads", indexes = {
    @jakarta.persistence.Index(name = "idx_user_id", columnList = "user_id")
})
public class ImageUpload {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "s3_key")
    private String s3Key;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ImageStatus status;

    @Column(name = "processing_error")
    private String processingError;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}
