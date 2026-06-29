package com.gathera.gathera.Locations;


import jakarta.validation.constraints.Min;

public record LocationSearchFilter(
        Long id,
        Integer capacity,
        @Min(3)
        Integer pageSize,
        @Min(0)
        Integer pageNumber
) {
}
