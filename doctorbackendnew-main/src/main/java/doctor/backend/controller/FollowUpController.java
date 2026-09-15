package doctor.backend.controller;

import doctor.backend.dto.followup.FollowUpRequest;
import doctor.backend.dto.followup.FollowUpResponse;
import doctor.backend.service.FollowUpService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/follow-ups")
@CrossOrigin(origins = "*")
public class FollowUpController {

    private final FollowUpService followUpService;

    public FollowUpController(FollowUpService followUpService) {
        this.followUpService = followUpService;
    }

    // =====================================================
    // CREATE
    // POST /api/follow-ups
    // =====================================================

    @PostMapping
    public ResponseEntity<FollowUpResponse> createFollowUp(
            @RequestBody FollowUpRequest request) {

        FollowUpResponse response =
                followUpService.createFollowUp(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    // =====================================================
    // GET ALL
    // GET /api/follow-ups
    // =====================================================

    @GetMapping
    public ResponseEntity<List<FollowUpResponse>> getAllFollowUps() {

        return ResponseEntity.ok(
                followUpService.getAllFollowUps()
        );
    }

    // =====================================================
    // GET BY ID
    // GET /api/follow-ups/{id}
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<FollowUpResponse> getFollowUpById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                followUpService.getFollowUpById(id)
        );
    }

    // =====================================================
    // GET BY PATIENT
    // GET /api/follow-ups/patient/{patientId}
    // =====================================================

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<FollowUpResponse>> getByPatient(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                followUpService.getByPatient(patientId)
        );
    }

    // =====================================================
    // GET BY STATUS
    // GET /api/follow-ups/status/{status}
    // =====================================================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<FollowUpResponse>> getByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                followUpService.getByStatus(status)
        );
    }

    // =====================================================
    // GET PATIENT + STATUS
    // GET /api/follow-ups/patient/{patientId}/status/{status}
    // =====================================================

    @GetMapping("/patient/{patientId}/status/{status}")
    public ResponseEntity<List<FollowUpResponse>>
    getPatientFollowUpsByStatus(
            @PathVariable Long patientId,
            @PathVariable String status) {

        return ResponseEntity.ok(
                followUpService.getPatientFollowUpsByStatus(
                        patientId,
                        status
                )
        );
    }

    // =====================================================
    // GET BY DATE
    // GET /api/follow-ups/date?date=2026-08-12
    // =====================================================

    @GetMapping("/date")
    public ResponseEntity<List<FollowUpResponse>> getFollowUpsOnDate(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        return ResponseEntity.ok(
                followUpService.getFollowUpsOnDate(date)
        );
    }

    // =====================================================
    // GET BEFORE DATE
    // GET /api/follow-ups/before?date=2026-08-12
    // =====================================================

    @GetMapping("/before")
    public ResponseEntity<List<FollowUpResponse>> getFollowUpsBefore(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        return ResponseEntity.ok(
                followUpService.getFollowUpsBefore(date)
        );
    }

    // =====================================================
    // GET DATE RANGE
    // GET /api/follow-ups/date-range
    // =====================================================

    @GetMapping("/date-range")
    public ResponseEntity<List<FollowUpResponse>>
    getFollowUpsBetween(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return ResponseEntity.ok(
                followUpService.getFollowUpsBetween(
                        startDate,
                        endDate
                )
        );
    }

    // =====================================================
    // GET UPCOMING
    // GET /api/follow-ups/upcoming
    // =====================================================

    @GetMapping("/upcoming")
    public ResponseEntity<List<FollowUpResponse>>
    getUpcomingFollowUps(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return ResponseEntity.ok(
                followUpService.getUpcomingFollowUps(
                        startDate,
                        endDate
                )
        );
    }

    // =====================================================
    // GET OVERDUE
    // GET /api/follow-ups/overdue?date=2026-08-12
    // =====================================================

    @GetMapping("/overdue")
    public ResponseEntity<List<FollowUpResponse>>
    getOverdueFollowUps(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        return ResponseEntity.ok(
                followUpService.getOverdueFollowUps(date)
        );
    }

    // =====================================================
    // GET BY APPOINTMENT
    // GET /api/follow-ups/appointment/{appointmentId}
    // =====================================================

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<List<FollowUpResponse>>
    getByAppointment(
            @PathVariable Long appointmentId) {

        return ResponseEntity.ok(
                followUpService.getByAppointment(
                        appointmentId
                )
        );
    }

    // =====================================================
    // PENDING REMINDERS
    // GET /api/follow-ups/reminders/pending
    // =====================================================

    @GetMapping("/reminders/pending")
    public ResponseEntity<List<FollowUpResponse>>
    getPendingReminders() {

        return ResponseEntity.ok(
                followUpService.getPendingReminders()
        );
    }

    // =====================================================
    // MARK REMINDER AS SENT
    // PATCH /api/follow-ups/{id}/reminder
    // =====================================================

    @PatchMapping("/{id}/reminder")
    public ResponseEntity<FollowUpResponse>
    markReminderAsSent(@PathVariable Long id) {

        return ResponseEntity.ok(
                followUpService.markReminderAsSent(id)
        );
    }

    // =====================================================
    // UPDATE
    // PUT /api/follow-ups/{id}
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<FollowUpResponse> updateFollowUp(
            @PathVariable Long id,
            @RequestBody FollowUpRequest request) {

        return ResponseEntity.ok(
                followUpService.updateFollowUp(
                        id,
                        request
                )
        );
    }

    // =====================================================
    // DELETE
    // DELETE /api/follow-ups/{id}
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFollowUp(
            @PathVariable Long id) {

        followUpService.deleteFollowUp(id);

        return ResponseEntity.noContent().build();
    }
}