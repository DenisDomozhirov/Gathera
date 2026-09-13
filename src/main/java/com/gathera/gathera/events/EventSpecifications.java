package com.gathera.gathera.events;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EventSpecifications {

    public EventSpecifications() {
    }

    public static Specification<EventsEntity> buildFrom(
            EventSearchRequestDto filter
    ){
        return Specification
                .allOf(
                        nameEquals(filter.name()),
                        statusEquals(filter.status()),
                        locationEquals(filter.locationId()),
                        placesBetween(filter.placesMin(), filter.placesMax()),
                        dateBetween(filter.dateStartBefore(), filter.dateStartAfter()),
                        costBetween(filter.costMin(), filter.costMax()),
                        durationBetween(filter.durationMin(), filter.durationMax())
                );
    }

    private static Specification<EventsEntity> nameEquals(
            String name
    ){
        return ((root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.equal(root.get("name"), name));
    }

    private static Specification<EventsEntity> statusEquals(
            EventStatus status
    ){
        return ((root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status));
    }
    private static Specification<EventsEntity> locationEquals(
            Long locationId
    ){
        return ((root, query, criteriaBuilder) ->
                locationId == null ? null : criteriaBuilder.equal(root.get("locations").get("id"), locationId));
    }
    private static Specification<EventsEntity> placesBetween(
            Integer min,
            Integer max
    ){
        return ((root, query, criteriaBuilder) -> {
            if(min == null && max == null) return null;
            if(min == null) return criteriaBuilder.lessThanOrEqualTo(root.get("maxPlaces"), max);
            if(max == null) return criteriaBuilder.lessThanOrEqualTo(root.get("maxPlaces"), min);
            return criteriaBuilder.between(root.get("maxPlaces"), min, max);
        });
    }
    private static Specification<EventsEntity> dateBetween(
            LocalDateTime after,
            LocalDateTime before
    ){
        return ((root, query, criteriaBuilder) -> {
            if(after == null && before == null) return null;
            if(after == null) return criteriaBuilder.lessThanOrEqualTo(root.get("startAt"), before);
            if(before == null) return criteriaBuilder.lessThanOrEqualTo(root.get("startAt"), after);
            return criteriaBuilder.between(root.get("startAt"), before, after);
        });
    }
    private static Specification<EventsEntity> costBetween(
            Integer min,
            Integer max
    ){
        return ((root, query, criteriaBuilder) -> {
            if(min == null && max == null) return null;
            var costPath = root.<BigDecimal>get("cost");
            if(min == null) return criteriaBuilder.le(costPath, BigDecimal.valueOf(max));
            if(max == null) return criteriaBuilder.ge(costPath, BigDecimal.valueOf(min));
            return criteriaBuilder.between(costPath, BigDecimal.valueOf(min), BigDecimal.valueOf(max));
        });
    }
    private static Specification<EventsEntity> durationBetween(
            Integer min,
            Integer max
    ){
        return ((root, query, criteriaBuilder) -> {
            if(min == null && max == null) return null;
            if(min == null) return criteriaBuilder.lessThanOrEqualTo(root.get("durationMinutes"), max);
            if(max == null) return criteriaBuilder.lessThanOrEqualTo(root.get("durationMinutes"), min);
            return criteriaBuilder.between(root.get("durationMinutes"), min, max);
        });
    }

}
