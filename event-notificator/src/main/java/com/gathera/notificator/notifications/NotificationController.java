package com.gathera.notificator.notifications;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationDto> getUnread(Authentication authentication){
        Long userId = (Long) authentication.getPrincipal();
        return notificationService.getUnread(userId);
    }

    @PostMapping
    public ResponseEntity<Void> markAsRead(
            Authentication authentication,
            @RequestBody MarkAsReadRequest readRequest
    ){
        Long userId = (Long) authentication.getPrincipal();
        notificationService.markSaRead(userId, readRequest.notificationIds());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
