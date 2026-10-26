package com.gathera.gathera.events;

import com.gathera.gathera.kafka.EventKafkaSender;
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
    private final EventConverter eventConverter;
    private final EventKafkaSender eventKafkaSender;

    public EventsStatusScheduler(EventRepository eventRepository, EventConverter eventConverter, EventKafkaSender eventKafkaSender) {
        this.eventRepository = eventRepository;
        this.eventConverter = eventConverter;
        this.eventKafkaSender = eventKafkaSender;
    }

    @Scheduled(fixedRate = 20_000)
    @Transactional
    public void transitionEventStatuses(){
        var now = LocalDateTime.now();

        int finished = 0;
        for(EventsEntity entity : eventRepository.findByStatus(EventStatus.STARTED)){
            var endAt = entity.getStartAt().plusMinutes(entity.getDurationMinutes());
            if(!endAt.isAfter(now)){
                changeStatus(entity, EventStatus.FINISHED);
                finished++;
            }
        }

        var eventsToStart = eventRepository.findByStatusAndStartAtLessThanEqual(EventStatus.WAIT_START, now);
        for(EventsEntity entity : eventsToStart){
            changeStatus(entity, EventStatus.STARTED);
        }


        if(!eventsToStart.isEmpty() || finished > 0){
            log.info("Scheduler: {} events started, {} events finished", eventsToStart.size(), finished);
        }
    }

    public void changeStatus(EventsEntity entity, EventStatus newStatus){
        var before = eventConverter.toDomain(entity);
        entity.setStatus(newStatus);
        var saved = eventRepository.save(entity);

        eventKafkaSender.publishUpdate(before, eventConverter.toDomain(saved), null);
    }
}
