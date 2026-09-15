package com.zenve.admin.repository;

import com.zenve.admin.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findAllByOrderByCreatedAtDesc();
    long countByReadFalse();

    @Modifying
    @Transactional
    @Query("update Notification n set n.read = true where n.read = false")
    void markAllRead();
}
