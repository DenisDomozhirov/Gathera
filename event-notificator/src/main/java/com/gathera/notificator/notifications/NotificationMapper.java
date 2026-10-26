package com.gathera.notificator.notifications;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationMapper {

    private final ObjectMapper objectMapper;

    public NotificationMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public NotificationDto toDto(NotificationEntity entity){
        var payloadEntity = entity.getPayload();
        JsonNode payload = readJson(payloadEntity.getPayloadJson());

        return new NotificationDto(
                entity.getId(),
                payloadEntity.getEventType(),
                payloadEntity.getEventId(),
                entity.getCreatedAt(),
                entity.isRead(),
                buildMessage(payload),
                payload
        );
    }

    private String buildMessage(JsonNode payload){
        List<String> fields = new ArrayList<>();
        for(JsonNode change : payload.path("changes")){
            fields.add(change.path("feild").asText());
        }
        return "Мероприятие изменено: %s".formatted(payload.path("eventName").asText());
    }

    private JsonNode readJson(String json){
        try{
            JsonNode node = objectMapper.readTree(json);
            if(node.isTextual()){
                node = objectMapper.readTree(node.asText());
            }
            return node;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot read notifications payload",e);
        }
    }
}
