package com.gathera.gathera.registration;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<RegistrationsEntity, Long> {

    List<RegistrationsEntity> findByUserId(Long userId);

    Optional<RegistrationsEntity> findByEventIdAndUserId(Long eventId, Long userId);

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

}
