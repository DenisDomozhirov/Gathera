package com.gathera.gathera.registration;

import com.gathera.gathera.Users.UserRepository;
import com.gathera.gathera.events.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final EventConverter eventConverter;

    public RegistrationService(EventRepository eventRepository,
                               RegistrationRepository registrationRepository,
                               UserRepository userRepository, EventConverter eventConverter) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
        this.eventConverter = eventConverter;
    }

    @Transactional
    public void registerOnEvent(Long userId, Long eventId){
        EventsEntity event = eventRepository.findByIdForUpdate(eventId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Event not found with id = %s".formatted(eventId)
                ));

        if(event.getStatus() != EventStatus.WAIT_START){
            throw new IllegalArgumentException(
                    "Event is not open for registration, current status = %s".formatted(event.getStatus())
            );
        }

        if(event.getOccupiedPlaces() >= event.getMaxPlaces()){
            throw new IllegalArgumentException(
                    "No free places left for this event");
        }

        if(registrationRepository.existsByEventIdAndUserId(eventId, userId)){
            throw new IllegalArgumentException(
                    "User is already registered for this event");
        }

        var registration = new RegistrationsEntity();
        registration.setEvent(event);
        registration.setUser(userRepository.getReferenceById(userId));
        registration.setCreatedAt(LocalDateTime.now());
        registrationRepository.save(registration);

        event.setOccupiedPlaces(event.getOccupiedPlaces() + 1);
        eventRepository.save(event);
    }

    @Transactional
    public void cancelRegistration(Long userId, Long eventId){
        EventsEntity event = eventRepository.findByIdForUpdate(eventId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Event not found with id = %s".formatted(eventId)
                ));

        if(event.getStatus() == EventStatus.STARTED || event.getStatus() == EventStatus.FINISHED){
            throw new IllegalArgumentException(
                    "Cannot cancel registration after event has started");
        }

        var registration = registrationRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Registration is not found for this event"));

        registrationRepository.delete(registration);

        event.setOccupiedPlaces(event.getOccupiedPlaces() - 1);
        eventRepository.save(event);
    }

    public List<Event> getMyRegisteredEvents(Long userId){
        return registrationRepository.findByUserId(userId)
                .stream()
                .map(reg -> eventConverter.toDomain(reg.getEvent()))
                .toList();
    }

}
