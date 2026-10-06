package com.gathera.gathera.events;


import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<EventsEntity, Long>, JpaSpecificationExecutor<EventsEntity> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from EventsEntity e where e.id = :id")
    Optional<EventsEntity> findByIdForUpdate(@Param("id") Long id);

    List<EventsEntity> findByOwnerId(Long ownerId);

    @Modifying
    @Query("update EventsEntity e set e.status = :toStatus where e.status = :fromStatus and e.startAt <= :now")
    int transitionStatus(
            @Param("fromStatus") EventStatus fromStatus,
            @Param("toStatus") EventStatus toStatus,
            @Param("now")LocalDateTime now
            );

    @Modifying
    @Query(value = """
            UPDATE events 
            SET status = 'FINISHED' 
            WHERE status = 'STARTED' 
            AND start_at + (duration_minutes || ' minutes') ::interval <= :now""", nativeQuery = true)
    int transitionFinishedEvents(@Param("now") LocalDateTime now);
}
