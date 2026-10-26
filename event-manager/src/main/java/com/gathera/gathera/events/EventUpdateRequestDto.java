package com.gathera.gathera.events;

import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

public record EventUpdateRequestDto(
        String name,
        @Min(1)
        Integer maxPlaces,
        LocalDateTime date,
        @Min(1)
        Integer cost,
        @Min(30)
        Integer duration,
        Long locationId
) {
}
