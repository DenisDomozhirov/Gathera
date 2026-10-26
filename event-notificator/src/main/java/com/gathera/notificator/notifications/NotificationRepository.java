package com.gathera.notificator.notifications;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @Query("""
        select n from NotificationEntity n
        join fetch n.payload
        where n.userId = :userId and n.isRead = false
        order by n.createdAt desc
    """)
    List<NotificationEntity> findUnreadByUserId(@Param("userId") Long userId);

    List<NotificationEntity> findByIdInAndUserId(Collection<Long> ids, Long userId);

    @Modifying
    @Query("delete from NotificationEntity n where n.createdAt < :threshold")
    int deleteOlderThan(@Param("threshold") LocalDateTime threshold);

}
