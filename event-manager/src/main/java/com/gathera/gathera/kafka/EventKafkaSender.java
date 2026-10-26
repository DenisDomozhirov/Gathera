package com.gathera.gathera.kafka;

import com.gathera.common.kafka.ChangeItem;
import com.gathera.common.kafka.EventChange;
import com.gathera.common.kafka.EventChangeKafkaMessage;
import com.gathera.gathera.events.Event;
import com.gathera.gathera.registration.RegistrationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class EventKafkaSender {

    private static final Logger log = LoggerFactory.getLogger(EventKafkaSender.class);

    private final KafkaTemplate<String, EventChangeKafkaMessage> kafkaTemplate;
    private final RegistrationRepository registrationRepository;

    public EventKafkaSender(KafkaTemplate<String, EventChangeKafkaMessage> kafkaTemplate, RegistrationRepository registrationRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.registrationRepository = registrationRepository;
    }

    public void publishUpdate(Event before, Event after, Long changeById){
        List<ChangeItem> changes = findChanges(before, after);
        if(changes.isEmpty()){
            return;
        }

        List<Long> subscribers = registrationRepository.findUserIdsByEventId(after.id());

        var message = new EventChangeKafkaMessage(
                UUID.randomUUID(),
                EventChange.EVENT_UPDATED,
                after.id(),
                after.name(),
                LocalDateTime.now(),
                after.ownerId(),
                changeById,
                subscribers,
                changes
        );
        // sendAfterCommit(message);
        send(message);
    }

    private List<ChangeItem> findChanges(Event before, Event after){
        List<ChangeItem> changes = new ArrayList<>();
        addIfChanged(changes, "name", before.name(), after.name());
        addIfChanged(changes, "date", before.startAt(), after.startAt());
        addIfChanged(changes, "duration", before.durationMinutes(), after.durationMinutes());
        addIfChanged(changes, "maxPlaces", before.maxPlaces(), after.maxPlaces());
        addIfChanged(changes, "cost", before.cost(), after.cost());
        addIfChanged(changes, "locationId", before.locationId(), after.locationId());
        addIfChanged(changes, "status", before.status(), after.status());
        return changes;
    }

    private void addIfChanged(List<ChangeItem> changes, String field, Object oldValue, Object newValue){
        if(!isSame(oldValue, newValue)){
            changes.add(new ChangeItem(field, asText(oldValue), asText(newValue)));
        }
    }

    private String asText(Object value) {
        if(value == null){
            return null;
        }
        if(value instanceof BigDecimal decimal){
            return decimal.toPlainString();
        }
        return value.toString();
    }

    private boolean isSame(Object oldValue, Object newValue) {
        if(oldValue instanceof BigDecimal x && newValue instanceof BigDecimal y){
            return x.compareTo(y) == 0;
        }
        return Objects.equals(oldValue, newValue);
    }

//    private void sendAfterCommit(EventChangeKafkaMessage message){
//        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//            @Override
//            public void afterCommit() {
//                send(message);
//            }
//        });
//    }

    private void send(EventChangeKafkaMessage message){
        try{
            kafkaTemplate.send(
                    "event-change",
                            String.valueOf(message.eventId()),
                            message).whenComplete((result, ex) -> {
                                if(ex != null) {
                                    log.error("Failed to send message {}", message.messageId(), ex);
                                }
            });
            log.info("Message {} for event {} sent to kafka", message.messageId(), message.eventId());
        } catch (Exception e){
            log.error("Message {} was not sent", message.messageId(), e);
        }
    }


}
