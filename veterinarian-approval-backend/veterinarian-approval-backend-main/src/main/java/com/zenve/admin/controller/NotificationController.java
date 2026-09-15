package com.zenve.admin.controller;

import com.zenve.admin.dto.NotificationsResponse;
import com.zenve.admin.service.NotificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public NotificationsResponse list() {
        return notificationService.list();
    }

    @PostMapping("/{id}/read")
    public void markRead(@PathVariable String id) {
        notificationService.markRead(id);
    }

    @PostMapping("/read-all")
    public void markAllRead() {
        notificationService.markAllRead();
    }
}
