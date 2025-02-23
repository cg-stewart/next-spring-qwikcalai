package com.qwikcalai.api.controller;

import com.qwikcalai.api.model.CalendarEvent;
import com.qwikcalai.api.model.ImageUpload;
import com.qwikcalai.api.service.OpenAIVisionService;
import com.qwikcalai.api.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageProcessingController {

    private final S3Service s3Service;
    private final OpenAIVisionService openAIVisionService;

    @PostMapping("/upload")
    public ResponseEntity<ImageUpload> uploadImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        ImageUpload upload = s3Service.uploadImage(file, userId);
        return ResponseEntity.ok(upload);
    }

    @PostMapping("/{imageId}/process")
    public ResponseEntity<List<CalendarEvent>> processImage(
            @PathVariable String imageId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        // TODO: Get image URL from ImageUpload repository
        String imageUrl = "https://example.com/image.jpg";
        List<CalendarEvent> events = openAIVisionService.extractEventsFromImage(imageUrl);
        return ResponseEntity.ok(events);
    }
}
