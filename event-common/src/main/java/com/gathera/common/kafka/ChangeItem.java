package com.gathera.common.kafka;


public record ChangeItem(
        String field,
        String oldValue,
        String newValue
) {
}
