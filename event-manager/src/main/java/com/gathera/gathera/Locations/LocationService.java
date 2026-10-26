package com.gathera.gathera.Locations;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class LocationService {

    private LocationsEntityConverter entityConverter;
    private LocationRepository locationRepository;

    public LocationService(LocationsEntityConverter entityConverter, LocationRepository locationRepository) {
        this.entityConverter = entityConverter;
        this.locationRepository = locationRepository;
    }

    public Locations createNewLocation(
            Locations locationToCreate
    ) {
        var createdLocation = entityConverter.toEntity(locationToCreate);
        var savedLocation = locationRepository.save(createdLocation);

        return entityConverter.toDomain(savedLocation);
    }

    public List<Locations> getAllLocations(
            LocationSearchFilter locationSearchFilter) {

        int pageSize = locationSearchFilter.pageSize() != null ? locationSearchFilter.pageSize() : 5;

        int pageNum = locationSearchFilter.pageNumber() != null ? locationSearchFilter.pageNumber() : 0;

        Pageable pageable = Pageable
                .ofSize(pageSize)
                .withPage(pageNum);

        return locationRepository.searchLocations(
                        locationSearchFilter.id(),
                        locationSearchFilter.capacity(),
                        pageable
                )
                .stream()
                .map(entityConverter::toDomain)
                .toList();
    }

    public Locations searchLocationById(Long id) {
        var foundedLocation = locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found location with this id"
                ));
        return entityConverter.toDomain(foundedLocation);
    }

    public void deleteLocationById(Long id) {
        if(!locationRepository.existsById(id)) throw new NoSuchElementException(
                "No founded location with this id=%s".formatted(id)
        );
        locationRepository.deleteById(id);
    }

    public Locations updateLocation(
        Long id,
        Locations locationToUpdate
    ){
        if(!locationRepository.existsById(id)) throw new NoSuchElementException(
                "No such element with this id=%s".formatted(id)
        );

        locationRepository.updateLocation(
                id,
                locationToUpdate.name(),
                locationToUpdate.address(),
                locationToUpdate.capacity()
        );
        return entityConverter.toDomain(locationRepository.findById(id).orElseThrow());
    }

}
