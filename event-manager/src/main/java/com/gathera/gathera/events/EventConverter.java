package com.gathera.gathera.events;

import com.gathera.gathera.Locations.LocationsEntity;
import com.gathera.gathera.Users.UserEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EventConverter {
    public Event toDomain(EventsEntity entity){
        return new Event(
                entity.getId(),
                entity.getName(),
                entity.getStartAt(),
                entity.getDurationMinutes(),
                entity.getMaxPlaces(),
                entity.getOccupiedPlaces(),
                entity.getCost(),
                entity.getStatus(),
                entity.getLocations().getId(),
                entity.getUsers().getId()
        );
    }

    public EventDto toDto(Event domain){
        return new EventDto(
                domain.id(),
                domain.name(),
                domain.ownerId(),
                domain.maxPlaces(),
                domain.occupiedPlaces(),
                domain.startAt(),
                domain.cost() != null ? domain.cost().intValue() : null,
                domain.durationMinutes(),
                domain.locationId(),
                domain.status()
        );
    }

    public EventsEntity buildForCreate(
            EventCreatedRequestDto eventCreatedRequestDto,
            LocationsEntity locations,
            UserEntity owner
    ){
        EventsEntity entity = new EventsEntity();
        entity.setName(eventCreatedRequestDto.name());
        entity.setStartAt(eventCreatedRequestDto.date());
        entity.setDurationMinutes(eventCreatedRequestDto.duration());
        entity.setMaxPlaces(eventCreatedRequestDto.maxPlaces());
        entity.setCost(BigDecimal.valueOf(eventCreatedRequestDto.cost()));
        entity.setOccupiedPlaces(0);
        entity.setStatus(EventStatus.WAIT_START);
        entity.setLocations(locations);
        entity.setUsers(owner);
        return entity;
    }

    public void applyUpdate(
            EventUpdateRequestDto dto,
            EventsEntity entity,
            LocationsEntity locations
    ){
        if(dto.name() != null){
            entity.setName(dto.name());
        }
        if(dto.date() != null){
            entity.setStartAt(dto.date());
        }
        if(dto.duration() != null){
            entity.setDurationMinutes(dto.duration());
        }
        if(dto.maxPlaces() != null){
            entity.setMaxPlaces(dto.maxPlaces());
        }
        if(dto.cost() != null){
            entity.setCost(BigDecimal.valueOf(dto.cost()));
        }
        if(locations != null){
            entity.setLocations(locations);
        }
    }
}
