package com.gathera.notificator.notifications;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gathera.common.kafka.EventChangeKafkaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationEventPayloadRepository payloadRepository;
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;
    private final NotificationMapper notificationMapper;

    public NotificationService(
            NotificationEventPayloadRepository payloadRepository,
            NotificationRepository notificationRepository,
            ObjectMapper objectMapper, NotificationMapper notificationMapper
    ) {
        this.payloadRepository = payloadRepository;
        this.notificationRepository = notificationRepository;
        this.objectMapper = objectMapper;
        this.notificationMapper = notificationMapper;
    }

    @Transactional
    public void saveFromMessage(EventChangeKafkaMessage message){
        if(payloadRepository.existsByMessageId(message.messageId())){
            log.info("Message {} already processed, skipping", message.messageId());
            return;
        }
        if(message.subscribers() == null || message.subscribers().isEmpty()){
            log.info("message {} has no subscribers, skipping", message.messageId());
            return;
        }
        var payload = new NotificationsEventPayloadEntity();
        payload.setMessageId(message.messageId());
        payload.setEventType(message.eventType().name());
        payload.setEventId(message.eventId());
        payload.setOccurredAt(message.occurredAt());
        payload.setOwnerId(message.ownerId());
        payload.setChangedById(message.changedById());
        payload.setPayloadJson(buildPayloadJson(message));
        var savedPayload = payloadRepository.save(payload);

        var now = LocalDateTime.now();
        List<NotificationEntity> notifications = message.subscribers().stream()
                .map(userId -> {
                    var notification = new NotificationEntity();
                    notification.setUserId(userId);
                    notification.setPayload(savedPayload);
                    notification.setRead(false);
                    notification.setCreatedAt(now);
                    return notification;
                })
                .toList();
        notificationRepository.saveAll(notifications);

        log.info("Message {}: saved 1 payload and {} notifications",
                message.messageId(), notifications.size());
    }

    private String buildPayloadJson(EventChangeKafkaMessage message){
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("eventName", message.name());
        payload.put("changedById", message.changedById());
        payload.put("changes", message.changes());
        try{
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e){
            throw new IllegalStateException("Cannot build for message " + message.messageId(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getUnread(Long userId){
        return notificationRepository.findUnreadByUserId(userId).stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    @Transactional
    public void markSaRead(Long userId, List<Long> notificationIds){
        if(notificationIds == null || notificationIds.isEmpty()){
            return;
        }

        var notifications = notificationRepository.findByIdInAndUserId(notificationIds, userId);

        var now = LocalDateTime.now();
        for(NotificationEntity notification : notifications){
            notification.setRead(true);
            notification.setReadAt(now);
        }
    }
}
