package com.gathera.gathera.events;

import com.gathera.gathera.security.jwt.AuthenticationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventsController {

    private final Logger log = LoggerFactory.getLogger(EventsController.class);

    private final EventConverter eventConverter;
    private final EventService eventService;
    private final AuthenticationService authenticationService;

    public EventsController(EventConverter eventConverter, EventService eventService, AuthenticationService authenticationService) {
        this.eventConverter = eventConverter;
        this.eventService = eventService;
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public ResponseEntity<EventDto> createNewEvent(
            @RequestBody @Valid EventCreatedRequestDto request
            ){
        log.info("Новое событие создано!");

        var currentUser = authenticationService.getCurrentAuthenticatedUserOrThrow();
        Event created = eventService.createNewEvent(request, currentUser);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventConverter.toDto(created));
    }

    @GetMapping("/{eventId}")
    public EventDto getEventById(
            @PathVariable("eventId") Long evebtId
    ){
        log.info("Get request for event with id = %s".formatted(evebtId));

        var event = eventService.getById(evebtId);

        return eventConverter.toDto(event);
    }

    @GetMapping("/my")
    public List<EventDto> getMyEvents(){
        var currentUser = authenticationService.getCurrentAuthenticatedUserOrThrow();
        log.info("Get request events with ownerId={}", currentUser.id());

        return eventService.getMy(currentUser.id())
                .stream()
                .map(eventConverter::toDto)
                .toList();
    }

    @PutMapping("/{eventId}")
    public EventDto updateEvent(
            @PathVariable("eventId") Long eventId,
            @RequestBody @Valid EventUpdateRequestDto request
    ){
        log.info("Put request for updating event with id={}", eventId);

        var currentUser = authenticationService.getCurrentAuthenticatedUserOrThrow();
        var updated = eventService.updateEvent(eventId, request, currentUser);

        return eventConverter.toDto(updated);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable("eventId") long eventId
    ){
        log.info("Delete request for event with id={}", eventId);

        var currentUser = authenticationService.getCurrentAuthenticatedUserOrThrow();
        eventService.deleteEvent(eventId, currentUser);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("/search")
    public List<EventDto> searchEvents(
            @RequestBody EventSearchRequestDto request
    ){
        log.info("Post request for search events with filter={}", request);

        return eventService.search(request)
                .stream()
                .map(eventConverter::toDto)
                .toList();
    }
}
