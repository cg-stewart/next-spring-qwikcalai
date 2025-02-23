package com.qwikcalai.api.service;

import com.qwikcalai.api.model.ImageStatus;
import com.qwikcalai.api.model.ImageUpload;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Template s3Template;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public ImageUpload uploadImage(MultipartFile file, String userId) {
        try {
            String fileExtension = getFileExtension(file.getOriginalFilename());
            String key = String.format("uploads/%s/%s%s", 
                userId, 
                UUID.randomUUID(), 
                fileExtension
            );

            s3Template.upload(bucketName, key, file.getInputStream());

            String s3Url = String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);

            ImageUpload upload = new ImageUpload();
            upload.setId(UUID.randomUUID().toString());
            upload.setUserId(userId);
            upload.setOriginalFileName(file.getOriginalFilename());
            upload.setS3Key(key);
            upload.setContentType(file.getContentType());
            upload.setFileSize(file.getSize());
            upload.setStatus(ImageStatus.UPLOADED);
            upload.setUploadedAt(LocalDateTime.now());

            return upload;
        } catch (IOException e) {
            log.error("Failed to upload file to S3", e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    private String getFileExtension(String filename) {
        return filename != null && filename.contains(".") 
            ? filename.substring(filename.lastIndexOf(".")) 
            : "";
    }
}
