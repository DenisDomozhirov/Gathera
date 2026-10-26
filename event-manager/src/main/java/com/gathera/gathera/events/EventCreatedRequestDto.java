package com.gathera.gathera.events;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EventCreatedRequestDto(
        @NotBlank
        String name,
        @NotNull
        @Min(1)
        Integer maxPlaces,
        @NotNull
        LocalDateTime date,
        @NotNull
        @Min(1)
        Integer cost,
        @NotNull
        @Min(30)
        Integer duration,
        @NotNull
        Long locationId
) {
}
