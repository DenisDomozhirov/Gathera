package com.gathera.gathera.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Event (
        Long id,
        String name,
        LocalDateTime startAt,
        Integer durationMinutes,
        Integer maxPlaces,
        Integer occupiedPlaces,
        BigDecimal cost,
        EventStatus status,
        Long locationId,
        Long ownerId
){
}
