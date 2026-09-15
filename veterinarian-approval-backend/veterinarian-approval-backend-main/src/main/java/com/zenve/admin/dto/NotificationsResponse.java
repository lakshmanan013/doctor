package com.zenve.admin.dto;

import java.util.List;

public record NotificationsResponse(List<NotificationDto> notifications, long unreadCount) {
}
