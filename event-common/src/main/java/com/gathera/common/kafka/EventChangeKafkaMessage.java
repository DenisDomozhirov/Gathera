package com.gathera.common.kafka;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record EventChangeKafkaMessage(
        UUID messageId,
        EventChange eventType,
        Long eventId,
        String name,
        LocalDateTime occurredAt,
        Long ownerId,
        Long changedById,
        List<Long> subscribers,
        List<ChangeItem> changes

) {
}
