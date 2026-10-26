package com.gathera.gathera.errors;

import java.time.LocalDateTime;

public record ServerDto (
        String message,
        String detailedMessage,
        LocalDateTime dateTime
){
}
