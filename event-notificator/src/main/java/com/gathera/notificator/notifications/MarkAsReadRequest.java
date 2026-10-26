package com.gathera.notificator.notifications;

import java.util.List;

public record MarkAsReadRequest(List<Long> notificationIds) {
}
