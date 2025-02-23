package com.qwikcalai.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qwikcalai.api.model.CalendarEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.Media;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIVisionService {

    private final OpenAiChatModel chatModel;
    private final ObjectMapper objectMapper;

    @Value("${spring.ai.openai.api-key}")
    private String openAiApiKey;

    public List<CalendarEvent> extractEventsFromImage(String imageUrl) {
        try {
            String promptText = """
                    Extract all calendar events from this image. For each event, identify:
                    1. Event name
                    2. Date and time
                    3. Location (if available)
                    4. Description (if available)

                    Format the response as a JSON array of events with the following structure:
                    [
                        {
                            "eventName": "string",
                            "startTime": "ISO-8601 datetime",
                            "endTime": "ISO-8601 datetime",
                            "location": "string",
                            "description": "string"
                        }
                    ]

                    If you can't determine a field, omit it from the JSON.
                    """;

            Message userMessage = new UserMessage(promptText,
                    List.of(new Media(
                            MimeTypeUtils.IMAGE_PNG,
                            new UrlResource(imageUrl))));

            ChatResponse response = chatClient.call(
                    new Prompt(
                            List.of(userMessage),
                            OpenAiChatOptions.builder()
                                    .model("gpt-4-vision-preview")
                                    .build()));

            String jsonResponse = response.getResult().getOutput().getContent();
            return objectMapper.readValue(jsonResponse, new TypeReference<List<CalendarEvent>>() {
            });
        } catch (IOException e) {
            log.error("Failed to process image or parse OpenAI response", e);
            return List.of();
        }
    }
}
