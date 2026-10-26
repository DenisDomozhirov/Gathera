package com.gathera.notificator.notifications;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class NotificationCleanUpScheduler {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(NotificationCleanUpScheduler.class);

    private final NotificationRepository notificationRepository;
    private final NotificationEventPayloadRepository payloadRepository;
    private final int retentionDays;

    public NotificationCleanUpScheduler(
            NotificationRepository notificationRepository,
            NotificationEventPayloadRepository payloadRepository,
            @Value("${notifications.retention-days}") int retentionDays
    ) {
        this.notificationRepository = notificationRepository;
        this.payloadRepository = payloadRepository;
        this.retentionDays = retentionDays;
    }

    @Scheduled(cron = "${notifications.cleanup.cron}")
    @Transactional
    public void cleanup(){
        var threshold = LocalDateTime.now().minusDays(retentionDays);

        int notifications = notificationRepository.deleteOlderThan(threshold);
        int payloads = payloadRepository.deleteWithoutNotifications();

        log.info("Cleanup: deleted {} notifications and {} payloads", notifications, payloads);
    }
}
