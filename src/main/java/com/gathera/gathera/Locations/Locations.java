package com.gathera.gathera.Locations;

import jakarta.persistence.criteria.CriteriaBuilder;

public record Locations(
        Long id,
        String name,
        String address,
        Integer capacity
) {
}
