package com.gathera.gathera.registration;


import com.gathera.gathera.events.EventConverter;
import com.gathera.gathera.events.EventDto;
import com.gathera.gathera.security.jwt.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/registrations")
public class RegistrationController {
    private final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    private final RegistrationService registrationService;
    private final AuthenticationService authenticationService;
    private final EventConverter eventConverter;

    public RegistrationController(RegistrationService registrationService,
                                  AuthenticationService authenticationService,
                                  EventConverter eventConverter) {
        this.registrationService = registrationService;
        this.authenticationService = authenticationService;
        this.eventConverter = eventConverter;
    }

    @PostMapping("/{eventId}")
    public ResponseEntity<Void> register(
            @PathVariable("eventId") Long eventId
    ){
        var currentUser = authenticationService.getCurrentAuthenticatedUserOrThrow();
        log.info("Post request to register userId = %s".formatted(currentUser.id()));

        registrationService.registerOnEvent(currentUser.id(), eventId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @DeleteMapping("/cancel/{eventId}")
    public ResponseEntity<Void> cancel(
            @PathVariable("eventId") Long eventId
    ){
        var currentUser = authenticationService.getCurrentAuthenticatedUserOrThrow();

        log.info("Delete request to cancel registration user with id = %s".formatted(currentUser.id()));

        registrationService.cancelRegistration(currentUser.id(), eventId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/my")
    public List<EventDto> getMyRegistrations(){
        var currentUser = authenticationService.getCurrentAuthenticatedUserOrThrow();
        log.info("Get request to register userId = %s".formatted(currentUser.id()));

        return registrationService.getMyRegisteredEvents(currentUser.id())
                .stream()
                .map(eventConverter::toDto)
                .toList();
    }

}
