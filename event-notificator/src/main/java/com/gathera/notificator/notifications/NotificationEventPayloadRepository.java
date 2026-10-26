package com.gathera.notificator.notifications;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface NotificationEventPayloadRepository extends JpaRepository<NotificationsEventPayloadEntity, Long> {

    boolean existsByMessageId(UUID messageId);

    @Modifying
    @Query(value = """
        delete from notification_event_payloads 
        where id not in (select payload_id from notifications)""", nativeQuery = true)
    int deleteWithoutNotifications();

}
