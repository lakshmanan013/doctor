package com.zenve.admin.service;

import com.zenve.admin.dto.NotificationDto;
import com.zenve.admin.dto.NotificationsResponse;
import com.zenve.admin.exception.ApiException;
import com.zenve.admin.model.Notification;
import com.zenve.admin.model.NotificationType;
import com.zenve.admin.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationsResponse list() {
        List<NotificationDto> notifications = notificationRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(NotificationDto::from)
                .toList();
        long unreadCount = notificationRepository.countByReadFalse();
        return new NotificationsResponse(notifications, unreadCount);
    }

    @Transactional
    public void markRead(String id) {
        Notification notification = notificationRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> ApiException.notFound("Notification not found"));
        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(java.util.Objects.requireNonNull(notification));
        }
    }

    @Transactional
    public void markAllRead() {
        notificationRepository.markAllRead();
    }

    /** Used internally whenever a doctor registers, is approved, or is rejected. */
    public void notify(NotificationType type, String title, String message, String doctorId) {
        Notification notification = Notification.builder()
                .type(type)
                .title(title)
                .message(message)
                .doctorId(doctorId)
                .read(false)
                .build();
        notificationRepository.save(java.util.Objects.requireNonNull(notification));
    }
}
