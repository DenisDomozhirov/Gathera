package com.gathera.gathera.Locations;

import org.springframework.stereotype.Component;

@Component
public class LocationsEntityConverter {

    public LocationsEntity toEntity(
            Locations locations
    ){
        return new LocationsEntity(
                locations.id(),
                locations.name(),
                locations.address(),
                locations.capacity()
        );
    }

    public Locations toDomain(
            LocationsEntity locationsEntity
    ){
        return new Locations(
                locationsEntity.getId(),
                locationsEntity.getName(),
                locationsEntity.getAddress(),
                locationsEntity.getCapacity()
        );
    }

}
