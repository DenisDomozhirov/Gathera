package com.gathera.gathera.events;

import com.gathera.gathera.Locations.LocationRepository;
import com.gathera.gathera.Locations.LocationsEntity;
import com.gathera.gathera.Users.User;
import com.gathera.gathera.Users.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final EventConverter eventConverter;
    private final EventPermissionService eventPermissionService;


    public EventService(EventRepository eventRepository,
                        LocationRepository locationRepository,
                        UserRepository userRepository,
                        EventConverter eventConverter,
                        EventPermissionService eventPermissionService) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.eventConverter = eventConverter;
        this.eventPermissionService = eventPermissionService;
    }

    @Transactional
    public Event createNewEvent(@Valid EventCreatedRequestDto dto, User currentUser) {
        LocationsEntity location = locationRepository.findById(dto.locationId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Location with this id = %s not found".formatted(dto.locationId())
                ));

        if(dto.maxPlaces() > location.getCapacity()){
            throw new IllegalArgumentException(
                    "maxPlaces (%s) exceeds location capacity (%s)"
                            .formatted(dto.maxPlaces(), location.getCapacity()));
        }

        var owner = userRepository.getReferenceById(currentUser.id());
        var entity = eventConverter.buildForCreate(dto, location, owner);
        var saved = eventRepository.save(entity);

        return eventConverter.toDomain(saved);
    }

    public Event getById(Long eventId){
        var entity = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Event with this id = %s not found".formatted(eventId)
                ));

        return eventConverter.toDomain(entity);
    }

    public List<Event> getMy(Long ownerId){
        return eventRepository.findByOwnerId(ownerId)
                .stream()
                .map(eventConverter::toDomain)
                .toList();
    }

    @Transactional
    public Event updateEvent(Long eventId, EventUpdateRequestDto dto, User currentUser){
        var entity = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with this id = %s not found".formatted(eventId)));

        eventPermissionService.checkCanModify(entity, currentUser);

        if(entity.getStatus() != EventStatus.WAIT_START){
            throw new IllegalArgumentException("Cannot update event after it has started");
        }

        LocationsEntity newLocation = null;
        if(dto.locationId() != null){
            newLocation = locationRepository.findById(dto.locationId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Location with this id = %s not found"
                            .formatted(dto.locationId()))
                    );
        }

        Integer effectiveMaxPlaces = dto.maxPlaces() != null ?
                dto.maxPlaces() : entity.getMaxPlaces();
        Integer effectiveCapacity = newLocation != null ?
                newLocation.getCapacity() : entity.getLocations().getCapacity();

        if(effectiveMaxPlaces > effectiveCapacity){
            throw new IllegalArgumentException(
                    "maxPlaces (%d) exceeds location capacity (%d)".formatted(effectiveMaxPlaces, effectiveCapacity)
            );
        }

        if(effectiveMaxPlaces < entity.getOccupiedPlaces()){
            throw new IllegalArgumentException(
                    "maxPlaces connot be less than already occupied places (%d)".formatted(entity.getOccupiedPlaces())
            );
        }

        eventConverter.applyUpdate(dto, entity, newLocation);
        var saved = eventRepository.save(entity);
        return eventConverter.toDomain(saved);
    }

    @Transactional
    public void deleteEvent(Long eventId, User currentUser){
        var entity = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cannot fount event with id = %s".formatted(eventId)
                ));

        eventPermissionService.checkCanModify(entity, currentUser);

        if(entity.getStatus() != EventStatus.WAIT_START){
            throw new IllegalArgumentException("Cannot cancel event after it has started");
        }

        entity.setStatus(EventStatus.CANCELLED);
        eventRepository.save(entity);
    }

}
