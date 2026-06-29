package com.gathera.gathera.Locations;

import com.gathera.gathera.errors.GlobalExceptionHandler;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationsController {

    private static final Logger log = LoggerFactory.getLogger(LocationsController.class);

    private final LocationsDtoConverter dtoConverter;
    private final LocationService locationService;

    public LocationsController(LocationsDtoConverter dtoConverter, LocationService locationService) {
        this.dtoConverter = dtoConverter;
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<LocationsDto> createLocations(
            @RequestBody @Valid LocationsDto locationsDto
    ){
        log.info("Post request created for location = {}", locationsDto);

        var newLocation = locationService.createNewLocation(
                dtoConverter.toDomain(locationsDto)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dtoConverter.toDto(newLocation));

    }

    @GetMapping("/{id}")
    public LocationsDto allLocations(
            @PathVariable("id") Long id
    ){
        log.info("Get request is work for finding locations by id");

        var foundLocation = locationService.searchLocationById(id);

        return dtoConverter.toDto(foundLocation);
    }

    @GetMapping
    public List<LocationsDto> searchLocationById(
            @Valid LocationSearchFilter locationSearchFilter
    ){
        log.info("Get request is work for all locations in DB");

        return locationService.getAllLocations(locationSearchFilter)
                .stream()
                .map(dtoConverter::toDto)
                .toList();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable("id") Long id
    ){
        log.info("Get delete request is work for finding locations by id");

        locationService.deleteLocationById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PutMapping("/{id}")
    public LocationsDto updateLocations(
            @PathVariable("id") Long id,
            @Valid @RequestBody LocationsDto locationToUpdate
    ){
        log.info("Get put request fo update entity with id={}",id);

        var updatedLocation = locationService.updateLocation(id,
                dtoConverter.toDomain(locationToUpdate));

    return dtoConverter.toDto(updatedLocation);
    }

}
