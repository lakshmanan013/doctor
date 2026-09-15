package doctor.backend.service;

import doctor.backend.dto.appointment.AppointmentRequest;
import doctor.backend.dto.appointment.AppointmentResponse;
import doctor.backend.entity.Appointment;
import doctor.backend.entity.Patient;
import doctor.backend.entity.User;
import doctor.backend.exception.ResourceNotFoundException;
import doctor.backend.repository.AppointmentRepository;
import doctor.backend.repository.PatientRepository;
import doctor.backend.security.CurrentUserProvider;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final CurrentUserProvider currentUserProvider;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            CurrentUserProvider currentUserProvider) {

        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.currentUserProvider = currentUserProvider;
    }

    // =====================================================
    // CREATE APPOINTMENT
    // =====================================================

    public AppointmentResponse createAppointment(
            AppointmentRequest request) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();
        User doctor = currentUserProvider.getCurrentDoctor();

        Patient patient = patientRepository.findByIdAndDoctorId(
                request.getPatientId(),
                doctorId
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Patient not found with id: "
                                + request.getPatientId()
                )
        );

        Appointment appointment = new Appointment();

        appointment.setDoctorId(doctorId);
        appointment.setPatient(patient);
        appointment.setAppointmentDate(
                request.getAppointmentDate()
        );
        appointment.setAppointmentTime(
                request.getAppointmentTime()
        );
        appointment.setAppointmentType(
                request.getAppointmentType()
        );
        appointment.setReason(
                request.getReason()
        );
        appointment.setStatus(
                request.getStatus()
        );
        appointment.setNotes(
                request.getNotes()
        );

        String doctorName = request.getDoctorName();
        if (doctorName == null || doctorName.isBlank()) {
            doctorName = doctor.getFullName();
        }
        appointment.setDoctorName(doctorName);

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        return mapToResponse(savedAppointment);
    }

    // =====================================================
    // GET ALL APPOINTMENTS
    // =====================================================

    public List<AppointmentResponse> getAllAppointments() {

        Long doctorId = currentUserProvider.getCurrentDoctorId();
        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET APPOINTMENT BY ID
    // =====================================================

    public AppointmentResponse getAppointmentById(Long id) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();
        Appointment appointment =
                appointmentRepository.findByIdAndDoctorId(id, doctorId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Appointment not found with id: "
                                                + id
                                )
                        );

        return mapToResponse(appointment);
    }

    // =====================================================
    // GET APPOINTMENTS BY PATIENT
    // =====================================================

    public List<AppointmentResponse> getAppointmentsByPatient(
            Long patientId) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();
        if (!patientRepository.existsByIdAndDoctorId(patientId, doctorId)) {
            throw new ResourceNotFoundException(
                    "Patient not found with id: " + patientId
            );
        }

        return appointmentRepository
                .findByPatientIdAndDoctorId(patientId, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET APPOINTMENTS BY DATE
    // =====================================================

    public List<AppointmentResponse> getAppointmentsByDate(
            LocalDate date) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();
        return appointmentRepository
                .findByAppointmentDateAndDoctorId(date, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET APPOINTMENTS BY STATUS
    // =====================================================

    public List<AppointmentResponse> getAppointmentsByStatus(
            String status) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();
        return appointmentRepository
                .findByStatusAndDoctorId(status, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET APPOINTMENTS BY DATE AND STATUS
    // =====================================================

    public List<AppointmentResponse> getAppointmentsByDateAndStatus(
            LocalDate date,
            String status) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();
        return appointmentRepository
                .findByAppointmentDateAndStatusAndDoctorId(
                        date,
                        status,
                        doctorId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET APPOINTMENTS BETWEEN DATES
    // =====================================================

    public List<AppointmentResponse> getAppointmentsBetweenDates(
            LocalDate startDate,
            LocalDate endDate) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();
        return appointmentRepository
                .findByAppointmentDateBetweenAndDoctorId(
                        startDate,
                        endDate,
                        doctorId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // UPDATE APPOINTMENT
    // =====================================================

    public AppointmentResponse updateAppointment(
            Long id,
            AppointmentRequest request) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();
        Appointment appointment =
                appointmentRepository.findByIdAndDoctorId(id, doctorId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Appointment not found with id: "
                                                + id
                                )
                        );

        Patient patient = patientRepository.findByIdAndDoctorId(
                request.getPatientId(),
                doctorId
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Patient not found with id: "
                                + request.getPatientId()
                )
        );

        appointment.setPatient(patient);

        appointment.setAppointmentDate(
                request.getAppointmentDate()
        );

        appointment.setAppointmentTime(
                request.getAppointmentTime()
        );

        appointment.setAppointmentType(
                request.getAppointmentType()
        );

        appointment.setReason(
                request.getReason()
        );

        appointment.setStatus(
                request.getStatus()
        );

        appointment.setNotes(
                request.getNotes()
        );

        if (request.getDoctorName() != null && !request.getDoctorName().isBlank()) {
            appointment.setDoctorName(request.getDoctorName());
        }

        Appointment updatedAppointment =
                appointmentRepository.save(appointment);

        return mapToResponse(updatedAppointment);
    }

    // =====================================================
    // DELETE APPOINTMENT
    // =====================================================

    public void deleteAppointment(Long id) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();
        Appointment appointment =
                appointmentRepository.findByIdAndDoctorId(id, doctorId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Appointment not found with id: " + id
                                )
                        );

        appointmentRepository.delete(appointment);
    }

    // =====================================================
    // ENTITY → RESPONSE
    // =====================================================

    private AppointmentResponse mapToResponse(
            Appointment appointment) {

        AppointmentResponse response =
                new AppointmentResponse();

        response.setId(appointment.getId());
        response.setDoctorId(appointment.getDoctorId());

        // =========================
        // Appointment Information
        // =========================

        response.setAppointmentDate(
                appointment.getAppointmentDate()
        );

        response.setAppointmentTime(
                appointment.getAppointmentTime()
        );

        response.setAppointmentType(
                appointment.getAppointmentType()
        );

        response.setReason(
                appointment.getReason()
        );

        response.setStatus(
                appointment.getStatus()
        );

        response.setNotes(
                appointment.getNotes()
        );

        response.setDoctorName(
                appointment.getDoctorName()
        );

        // =========================
        // Patient Information
        // =========================

        Patient patient = appointment.getPatient();

        if (patient != null) {

            response.setPatientId(
                    patient.getId()
            );

            response.setPatientName(
                    patient.getName()
            );

            response.setSpecies(
                    patient.getSpecies()
            );

            response.setBreed(
                    patient.getBreed()
            );

            response.setIcon(
                    patient.getIcon()
            );

            // =========================
            // Owner Information
            // =========================

            if (patient.getOwner() != null) {

                response.setOwnerId(
                        patient.getOwner().getId()
                );

                response.setOwnerName(
                        patient.getOwner().getFullName()
                );

                response.setOwnerPhone(
                        patient.getOwner().getPhone()
                );
            }
        }

        // =========================
        // Timestamps
        // =========================

        response.setCreatedAt(
                appointment.getCreatedAt()
        );

        response.setUpdatedAt(
                appointment.getUpdatedAt()
        );

        return response;
    }
}