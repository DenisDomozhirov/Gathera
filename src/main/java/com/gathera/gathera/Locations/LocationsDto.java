package com.gathera.gathera.Locations;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LocationsDto (
        @Null
        Long id,
        @Size(max = 20)
        @NotBlank
        String name,
        @Size(max = 20)
        @NotBlank
        String address,
        @Min(0)
        @Max(100000)
        Integer capacity
)

{
}
