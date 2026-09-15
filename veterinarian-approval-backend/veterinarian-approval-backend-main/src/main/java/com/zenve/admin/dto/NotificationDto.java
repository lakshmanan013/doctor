package com.zenve.admin.dto;

import com.zenve.admin.model.Notification;

public record NotificationDto(
        String id,
        String type,
        String title,
        String message,
        boolean read,
        String doctorId,
        String createdAt
) {
    public static NotificationDto from(Notification n) {
        return new NotificationDto(
                n.getId(),
                n.getType().name(),
                n.getTitle(),
                n.getMessage(),
                n.isRead(),
                n.getDoctorId(),
                n.getCreatedAt() != null ? n.getCreatedAt().toString() : null
        );
    }
}
