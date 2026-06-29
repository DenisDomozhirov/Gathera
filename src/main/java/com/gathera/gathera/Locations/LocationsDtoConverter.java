package com.gathera.gathera.Locations;

import org.springframework.stereotype.Component;

@Component
public class LocationsDtoConverter {

    public LocationsDto toDto(
            Locations locations
    ){
        return new LocationsDto(
                locations.id(),
                locations.name(),
                locations.address(),
                locations.capacity()
        );
    }

    public Locations toDomain(
            LocationsDto locationsDto
    ){
        return new Locations(
                locationsDto.id(),
                locationsDto.name(),
                locationsDto.address(),
                locationsDto.capacity()
        );
    }

}
