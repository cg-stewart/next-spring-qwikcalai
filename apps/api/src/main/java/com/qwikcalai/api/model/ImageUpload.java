package com.qwikcalai.api.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@DynamoDBTable(tableName = "image_uploads")
public class ImageUpload {
    @DynamoDBHashKey
    private String id;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "UserIdIndex")
    private String userId;

    @DynamoDBAttribute
    private String originalFileName;

    @DynamoDBAttribute
    private String s3Key;

    @DynamoDBAttribute
    private String contentType;

    @DynamoDBAttribute
    private Long fileSize;

    @DynamoDBAttribute
    private String status; // UPLOADED, PROCESSING, COMPLETED, FAILED

    @DynamoDBAttribute
    private String processingError;

    @DynamoDBAttribute
    private LocalDateTime uploadedAt;

    @DynamoDBAttribute
    private LocalDateTime processedAt;
}
