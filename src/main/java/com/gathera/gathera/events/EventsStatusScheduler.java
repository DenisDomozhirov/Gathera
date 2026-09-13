package com.gathera.gathera.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class EventsStatusScheduler {

    private final Logger log = LoggerFactory.getLogger(EventsStatusScheduler.class);

    private final EventRepository eventRepository;

    public EventsStatusScheduler(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(fixedRate = 20_000)
    @Transactional
    public void transitionEventStatuses(){
        var now = LocalDateTime.now();

        int started = eventRepository.transitionStatus(EventStatus.WAIT_START, EventStatus.STARTED, now);
        int finished = eventRepository.transitionFinishedEvents(now);

        if(started > 0 || finished > 0){
            log.info("Scheduler: {} events started, {} events finished", started, finished);
        }
    }
}
