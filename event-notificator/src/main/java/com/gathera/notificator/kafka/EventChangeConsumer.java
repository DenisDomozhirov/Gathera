package com.gathera.notificator.kafka;

import com.gathera.common.kafka.EventChangeKafkaMessage;
import com.gathera.notificator.notifications.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventChangeConsumer {
    private static final Logger log = LoggerFactory.getLogger(EventChangeConsumer.class);
    private final NotificationService notificationService;

    public EventChangeConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "event-change")
    public void onMessage(EventChangeKafkaMessage message){
        log.info("Received message {} for event {}", message.messageId(), message.eventId());
        notificationService.saveFromMessage(message);
    }

}
