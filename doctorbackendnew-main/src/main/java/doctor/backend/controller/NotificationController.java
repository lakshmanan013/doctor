package doctor.backend.controller;

import doctor.backend.dto.notification.NotificationRequest;
import doctor.backend.dto.notification.NotificationResponse;
import doctor.backend.service.NotificationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    // =====================================================
    // CREATE
    // POST /api/notifications
    // =====================================================

    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(
            @RequestBody NotificationRequest request) {

        NotificationResponse response =
                notificationService.createNotification(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    // =====================================================
    // GET ALL
    // GET /api/notifications
    // =====================================================

    @GetMapping
    public ResponseEntity<List<NotificationResponse>>
    getAllNotifications() {

        return ResponseEntity.ok(
                notificationService.getAllNotifications()
        );
    }

    // =====================================================
    // GET BY ID
    // GET /api/notifications/{id}
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse>
    getNotificationById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                notificationService.getNotificationById(id)
        );
    }

    // =====================================================
    // GET BY USER
    // GET /api/notifications/user/{userId}
    // =====================================================

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>>
    getByUser(@PathVariable Long userId) {

        return ResponseEntity.ok(
                notificationService.getByUser(userId)
        );
    }

    // =====================================================
    // GET UNREAD
    // GET /api/notifications/unread
    // =====================================================

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>>
    getUnread() {

        return ResponseEntity.ok(
                notificationService.getUnread()
        );
    }

    // =====================================================
    // GET UNREAD BY USER
    // GET /api/notifications/user/{userId}/unread
    // =====================================================

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponse>>
    getUnreadByUser(@PathVariable Long userId) {

        return ResponseEntity.ok(
                notificationService.getUnreadByUser(userId)
        );
    }

    // =====================================================
    // GET READ
    // GET /api/notifications/read
    // =====================================================

    @GetMapping("/read")
    public ResponseEntity<List<NotificationResponse>>
    getRead() {

        return ResponseEntity.ok(
                notificationService.getRead()
        );
    }

    // =====================================================
    // GET BY TYPE
    // GET /api/notifications/type/{type}
    // =====================================================

    @GetMapping("/type/{type}")
    public ResponseEntity<List<NotificationResponse>>
    getByType(@PathVariable String type) {

        return ResponseEntity.ok(
                notificationService.getByType(type)
        );
    }

    // =====================================================
    // GET BY PRIORITY
    // GET /api/notifications/priority/{priority}
    // =====================================================

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<NotificationResponse>>
    getByPriority(@PathVariable String priority) {

        return ResponseEntity.ok(
                notificationService.getByPriority(priority)
        );
    }

    // =====================================================
    // GET BY PATIENT
    // GET /api/notifications/patient/{patientId}
    // =====================================================

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<NotificationResponse>>
    getByPatient(@PathVariable Long patientId) {

        return ResponseEntity.ok(
                notificationService.getByPatient(patientId)
        );
    }

    // =====================================================
    // GET BY APPOINTMENT
    // GET /api/notifications/appointment/{appointmentId}
    // =====================================================

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<List<NotificationResponse>>
    getByAppointment(@PathVariable Long appointmentId) {

        return ResponseEntity.ok(
                notificationService.getByAppointment(
                        appointmentId
                )
        );
    }

    // =====================================================
    // MARK AS READ
    // PUT /api/notifications/{id}/read
    // =====================================================

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse>
    markAsRead(@PathVariable Long id) {

        return ResponseEntity.ok(
                notificationService.markAsRead(id)
        );
    }

    // =====================================================
    // MARK AS UNREAD
    // PUT /api/notifications/{id}/unread
    // =====================================================

    @PutMapping("/{id}/unread")
    public ResponseEntity<NotificationResponse>
    markAsUnread(@PathVariable Long id) {

        return ResponseEntity.ok(
                notificationService.markAsUnread(id)
        );
    }

    // =====================================================
    // MARK ALL AS READ
    // PUT /api/notifications/user/{userId}/read-all
    // =====================================================

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @PathVariable Long userId) {

        notificationService.markAllAsRead(userId);

        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // DELETE
    // DELETE /api/notifications/{id}
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long id) {

        notificationService.deleteNotification(id);

        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // DELETE ALL USER NOTIFICATIONS
    // DELETE /api/notifications/user/{userId}
    // =====================================================

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteUserNotifications(
            @PathVariable Long userId) {

        notificationService.deleteUserNotifications(userId);

        return ResponseEntity.noContent().build();
    }
}