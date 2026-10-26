package com.gathera.gathera.events;


import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<EventsEntity, Long>, JpaSpecificationExecutor<EventsEntity> {

    @Query("select e from EventsEntity e where e.id = :id")
    Optional<EventsEntity> findByIdForUpdate(@Param("id") Long id);

    List<EventsEntity> findByOwnerId(Long ownerId);

    List<EventsEntity> findByStatusAndStartAtLessThanEqual(EventStatus status, LocalDateTime now);

    List<EventsEntity> findByStatus(EventStatus status);
}
