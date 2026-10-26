package com.gathera.gathera.registration;

import java.time.LocalDateTime;

public record Registration (
        Long id,
        Long eventId,
        Long userId,
        LocalDateTime createdAt
) {}
