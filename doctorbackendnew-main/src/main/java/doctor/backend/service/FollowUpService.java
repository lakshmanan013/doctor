package doctor.backend.service;

import doctor.backend.dto.followup.FollowUpRequest;
import doctor.backend.dto.followup.FollowUpResponse;
import doctor.backend.dto.notification.NotificationRequest;
import doctor.backend.entity.Appointment;
import doctor.backend.entity.FollowUp;
import doctor.backend.entity.Patient;
import doctor.backend.repository.AppointmentRepository;
import doctor.backend.repository.FollowUpRepository;
import doctor.backend.repository.PatientRepository;
import doctor.backend.security.CurrentUserProvider;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class FollowUpService {

    private final FollowUpRepository followUpRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;
    private final CurrentUserProvider currentUserProvider;

    public FollowUpService(
            FollowUpRepository followUpRepository,
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            NotificationService notificationService,
            CurrentUserProvider currentUserProvider) {

        this.followUpRepository = followUpRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.notificationService = notificationService;
        this.currentUserProvider = currentUserProvider;
    }

    // =====================================================
    // CREATE FOLLOW-UP
    // =====================================================

    public FollowUpResponse createFollowUp(
            FollowUpRequest request) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        if (request.getPatientId() == null) {
            throw new RuntimeException("Patient ID is required");
        }

        Patient patient = patientRepository
                .findByIdAndDoctorId(request.getPatientId(), doctorId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Patient not found with id: "
                                        + request.getPatientId()
                        )
                );

        FollowUp followUp = new FollowUp();

        followUp.setPatient(patient);
        followUp.setDoctorId(doctorId);

        // Appointment is optional
        if (request.getAppointmentId() != null) {

            Appointment appointment =
                    appointmentRepository
                            .findByIdAndDoctorId(request.getAppointmentId(), doctorId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Appointment not found with id: "
                                                    + request.getAppointmentId()
                                    )
                            );

            followUp.setAppointment(appointment);
        }

        followUp.setFollowUpDate(
                request.getFollowUpDate()
        );

        followUp.setNextFollowUpDate(
                request.getNextFollowUpDate()
        );

        followUp.setReason(
                request.getReason()
        );

        followUp.setSymptoms(
                request.getSymptoms()
        );

        followUp.setFindings(
                request.getFindings()
        );

        followUp.setTreatment(
                request.getTreatment()
        );

        followUp.setRecommendations(
                request.getRecommendations()
        );

        followUp.setNotes(
                request.getNotes()
        );

        followUp.setDoctorName(
                request.getDoctorName() != null && !request.getDoctorName().isBlank()
                        ? request.getDoctorName()
                        : currentUserProvider.getCurrentUser().getFullName()
        );

        if (request.getStatus() != null
                && !request.getStatus().trim().isEmpty()) {

            followUp.setStatus(
                    request.getStatus()
            );

        } else {

            followUp.setStatus("Scheduled");
        }

        if (request.getReminderSent() != null) {

            followUp.setReminderSent(
                    request.getReminderSent()
            );

        } else {

            followUp.setReminderSent(false);
        }

        FollowUp savedFollowUp =
                followUpRepository.save(followUp);

        // Let the doctor know a check-in has been scheduled, without
        // blocking the follow-up itself if this fails for any reason.
        try {
            NotificationRequest notification = new NotificationRequest();
            notification.setTitle("Follow-up scheduled");
            notification.setMessage(
                    (patient.getName() != null ? patient.getName() : "A patient")
                            + " is due for a follow-up"
                            + (savedFollowUp.getNextFollowUpDate() != null
                                    ? " on " + savedFollowUp.getNextFollowUpDate()
                                    : "")
                            + (savedFollowUp.getReason() != null
                                    ? " (" + savedFollowUp.getReason() + ")"
                                    : "")
            );
            notification.setType("followup");
            notification.setPatientId(patient.getId());
            notificationService.createNotification(notification);
        } catch (Exception ex) {
            // Non-critical — the follow-up itself is what matters.
        }

        return mapToResponse(savedFollowUp);
    }

    // =====================================================
    // GET ALL FOLLOW-UPS
    // =====================================================

    @Transactional(readOnly = true)
    public List<FollowUpResponse> getAllFollowUps() {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return followUpRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET FOLLOW-UP BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public FollowUpResponse getFollowUpById(Long id) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        FollowUp followUp =
                followUpRepository.findByIdAndDoctorId(id, doctorId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Follow-up not found with id: "
                                                + id
                                )
                        );

        return mapToResponse(followUp);
    }

    // =====================================================
    // GET BY PATIENT
    // =====================================================

    @Transactional(readOnly = true)
    public List<FollowUpResponse> getByPatient(
            Long patientId) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        if (!patientRepository.existsByIdAndDoctorId(patientId, doctorId)) {
            throw new RuntimeException(
                    "Patient not found with id: " + patientId
            );
        }

        return followUpRepository
                .findByPatientIdAndDoctorId(patientId, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY STATUS
    // =====================================================

    @Transactional(readOnly = true)
    public List<FollowUpResponse> getByStatus(
            String status) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return followUpRepository
                .findByStatusAndDoctorId(status, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET PATIENT FOLLOW-UPS BY STATUS
    // =====================================================

    @Transactional(readOnly = true)
    public List<FollowUpResponse>
    getPatientFollowUpsByStatus(
            Long patientId,
            String status) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        if (!patientRepository.existsByIdAndDoctorId(patientId, doctorId)) {
            throw new RuntimeException(
                    "Patient not found with id: " + patientId
            );
        }

        return followUpRepository
                .findByPatientIdAndStatusAndDoctorId(
                        patientId,
                        status,
                        doctorId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY FOLLOW-UP DATE
    // =====================================================

    @Transactional(readOnly = true)
    public List<FollowUpResponse>
    getFollowUpsOnDate(LocalDate date) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return followUpRepository
                .findByFollowUpDateAndDoctorId(date, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BEFORE DATE
    // =====================================================

    @Transactional(readOnly = true)
    public List<FollowUpResponse>
    getFollowUpsBefore(LocalDate date) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return followUpRepository
                .findByFollowUpDateBeforeAndDoctorId(date, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BETWEEN DATES
    // =====================================================

    @Transactional(readOnly = true)
    public List<FollowUpResponse>
    getFollowUpsBetween(
            LocalDate startDate,
            LocalDate endDate) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return followUpRepository
                .findByFollowUpDateBetweenAndDoctorId(
                        startDate,
                        endDate,
                        doctorId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET UPCOMING FOLLOW-UPS
    // =====================================================

    @Transactional(readOnly = true)
    public List<FollowUpResponse>
    getUpcomingFollowUps(
            LocalDate startDate,
            LocalDate endDate) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return followUpRepository
                .findByNextFollowUpDateBetweenAndDoctorId(
                        startDate,
                        endDate,
                        doctorId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET OVERDUE FOLLOW-UPS
    // =====================================================

    @Transactional(readOnly = true)
    public List<FollowUpResponse>
    getOverdueFollowUps(LocalDate date) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return followUpRepository
                .findByNextFollowUpDateBeforeAndDoctorId(date, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY APPOINTMENT
    // =====================================================

    @Transactional(readOnly = true)
    public List<FollowUpResponse>
    getByAppointment(Long appointmentId) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        if (!appointmentRepository.existsByIdAndDoctorId(appointmentId, doctorId)) {
            throw new RuntimeException(
                    "Appointment not found with id: " + appointmentId
            );
        }

        return followUpRepository
                .findByAppointmentIdAndDoctorId(appointmentId, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET PENDING REMINDERS
    // =====================================================

    @Transactional(readOnly = true)
    public List<FollowUpResponse>
    getPendingReminders() {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return followUpRepository
                .findByReminderSentFalseAndDoctorId(doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // UPDATE FOLLOW-UP
    // =====================================================

    public FollowUpResponse updateFollowUp(
            Long id,
            FollowUpRequest request) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        FollowUp followUp =
                followUpRepository.findByIdAndDoctorId(id, doctorId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Follow-up not found with id: "
                                                + id
                                )
                        );

        if (request.getPatientId() != null) {

            Patient patient =
                    patientRepository
                            .findByIdAndDoctorId(request.getPatientId(), doctorId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Patient not found with id: "
                                                    + request.getPatientId()
                                    )
                            );

            followUp.setPatient(patient);
        }

        if (request.getAppointmentId() != null) {

            Appointment appointment =
                    appointmentRepository
                            .findByIdAndDoctorId(request.getAppointmentId(), doctorId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Appointment not found with id: "
                                                    + request.getAppointmentId()
                                    )
                            );

            followUp.setAppointment(appointment);
        }

        followUp.setDoctorId(doctorId);

        followUp.setFollowUpDate(
                request.getFollowUpDate()
        );

        followUp.setNextFollowUpDate(
                request.getNextFollowUpDate()
        );

        followUp.setReason(
                request.getReason()
        );

        followUp.setStatus(
                request.getStatus()
        );

        followUp.setSymptoms(
                request.getSymptoms()
        );

        followUp.setFindings(
                request.getFindings()
        );

        followUp.setTreatment(
                request.getTreatment()
        );

        followUp.setRecommendations(
                request.getRecommendations()
        );

        followUp.setNotes(
                request.getNotes()
        );

        if (request.getDoctorName() != null && !request.getDoctorName().isBlank()) {
            followUp.setDoctorName(request.getDoctorName());
        }

        if (request.getReminderSent() != null) {

            followUp.setReminderSent(
                    request.getReminderSent()
            );
        }

        FollowUp updatedFollowUp =
                followUpRepository.save(followUp);

        return mapToResponse(updatedFollowUp);
    }

    // =====================================================
    // MARK REMINDER AS SENT
    // =====================================================

    public FollowUpResponse markReminderAsSent(Long id) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        FollowUp followUp =
                followUpRepository.findByIdAndDoctorId(id, doctorId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Follow-up not found with id: "
                                                + id
                                )
                        );

        followUp.setReminderSent(true);

        FollowUp updatedFollowUp =
                followUpRepository.save(followUp);

        return mapToResponse(updatedFollowUp);
    }

    // =====================================================
    // DELETE
    // =====================================================

    public void deleteFollowUp(Long id) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        FollowUp followUp = followUpRepository.findByIdAndDoctorId(id, doctorId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Follow-up not found with id: " + id
                        )
                );

        followUpRepository.delete(followUp);
    }

    // =====================================================
    // ENTITY -> RESPONSE
    // =====================================================

    private FollowUpResponse mapToResponse(
            FollowUp followUp) {

        FollowUpResponse response =
                new FollowUpResponse();

        response.setId(
                followUp.getId()
        );
        response.setDoctorId(
                followUp.getDoctorId()
        );

        if (followUp.getPatient() != null) {

            response.setPatientId(
                    followUp.getPatient().getId()
            );

            response.setPatientName(
                    followUp.getPatient().getName()
            );
        }

        if (followUp.getAppointment() != null) {

            response.setAppointmentId(
                    followUp.getAppointment().getId()
            );
        }

        response.setFollowUpDate(
                followUp.getFollowUpDate()
        );

        response.setNextFollowUpDate(
                followUp.getNextFollowUpDate()
        );

        response.setReason(
                followUp.getReason()
        );

        response.setStatus(
                followUp.getStatus()
        );

        response.setSymptoms(
                followUp.getSymptoms()
        );

        response.setFindings(
                followUp.getFindings()
        );

        response.setTreatment(
                followUp.getTreatment()
        );

        response.setRecommendations(
                followUp.getRecommendations()
        );

        response.setNotes(
                followUp.getNotes()
        );

        response.setDoctorName(
                followUp.getDoctorName()
        );

        response.setReminderSent(
                followUp.getReminderSent()
        );

        response.setCreatedAt(
                followUp.getCreatedAt()
        );

        response.setUpdatedAt(
                followUp.getUpdatedAt()
        );

        return response;
    }
}