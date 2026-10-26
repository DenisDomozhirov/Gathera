package com.gathera.gathera.registration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<RegistrationsEntity, Long> {

    List<RegistrationsEntity> findByUserId(Long userId);

    Optional<RegistrationsEntity> findByEventIdAndUserId(Long eventId, Long userId);

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    @Query("select r.user.id from RegistrationsEntity r where r.event.id = :eventId")
    List<Long> findUserIdsByEventId(@Param("eventId") Long eventId);
}
