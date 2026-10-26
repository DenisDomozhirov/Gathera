package com.gathera.notificator.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

public record NotificationDto (
        Long notificationId,
        String type,
        Long eventId,
        LocalDateTime createdAt,
        @JsonProperty("isRead") boolean isRead,
        String message,
        JsonNode payload
){
}
