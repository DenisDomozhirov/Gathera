package com.gathera.notificator.errors;

import java.time.LocalDateTime;

public record ErrorMessageResponse(
        String message,
        String detailedMessage,
        LocalDateTime dateTime
){
}
