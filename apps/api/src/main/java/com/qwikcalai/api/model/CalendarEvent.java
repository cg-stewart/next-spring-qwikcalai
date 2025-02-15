package com.qwikcalai.api.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@DynamoDBTable(tableName = "calendar_events")
public class CalendarEvent {
    @DynamoDBHashKey
    private String id;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "UserIdIndex")
    private String userId;

    @DynamoDBAttribute
    private String eventName;

    @DynamoDBAttribute
    private String location;

    @DynamoDBAttribute
    private LocalDateTime startTime;

    @DynamoDBAttribute
    private LocalDateTime endTime;

    @DynamoDBAttribute
    private String description;

    @DynamoDBAttribute
    private String sourceImageUrl;

    @DynamoDBAttribute
    private String icsFileUrl;

    @DynamoDBAttribute
    private LocalDateTime createdAt;

    @DynamoDBAttribute
    private LocalDateTime updatedAt;
}
