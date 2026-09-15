package doctor.backend.service;

import doctor.backend.dto.vaccination.VaccinationRequest;
import doctor.backend.dto.vaccination.VaccinationResponse;
import doctor.backend.entity.Patient;
import doctor.backend.entity.Vaccination;
import doctor.backend.repository.PatientRepository;
import doctor.backend.repository.VaccinationRepository;
import doctor.backend.security.CurrentUserProvider;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class VaccinationService {

    private final VaccinationRepository vaccinationRepository;
    private final PatientRepository patientRepository;
    private final CurrentUserProvider currentUserProvider;

    public VaccinationService(
            VaccinationRepository vaccinationRepository,
            PatientRepository patientRepository,
            CurrentUserProvider currentUserProvider) {

        this.vaccinationRepository = vaccinationRepository;
        this.patientRepository = patientRepository;
        this.currentUserProvider = currentUserProvider;
    }

    // =====================================================
    // CREATE VACCINATION
    // =====================================================

    public VaccinationResponse createVaccination(
            VaccinationRequest request) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        if (request.getPatientId() == null) {
            throw new RuntimeException("Patient ID is required");
        }

        if (request.getVaccineName() == null ||
                request.getVaccineName().trim().isEmpty()) {

            throw new RuntimeException(
                    "Vaccine name is required"
            );
        }

        Patient patient = patientRepository
                .findByIdAndDoctorId(request.getPatientId(), doctorId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Patient not found with id: "
                                        + request.getPatientId()
                        )
                );

        Vaccination vaccination = new Vaccination();

        vaccination.setPatient(patient);
        vaccination.setDoctorId(doctorId);
        vaccination.setVaccineName(request.getVaccineName());
        vaccination.setVaccineType(request.getVaccineType());
        vaccination.setManufacturer(request.getManufacturer());
        vaccination.setBatchNumber(request.getBatchNumber());
        vaccination.setVaccinationDate(
                request.getVaccinationDate()
        );
        vaccination.setNextDueDate(
                request.getNextDueDate()
        );
        vaccination.setDosage(request.getDosage());
        vaccination.setRoute(request.getRoute());
        vaccination.setAdministeredBy(
                request.getAdministeredBy() != null && !request.getAdministeredBy().isBlank()
                        ? request.getAdministeredBy()
                        : currentUserProvider.getCurrentUser().getFullName()
        );
        vaccination.setNotes(request.getNotes());

        if (request.getStatus() != null &&
                !request.getStatus().trim().isEmpty()) {

            vaccination.setStatus(request.getStatus());

        } else {

            vaccination.setStatus("Scheduled");
        }

        Vaccination savedVaccination =
                vaccinationRepository.save(vaccination);

        return mapToResponse(savedVaccination);
    }

    // =====================================================
    // GET ALL VACCINATIONS
    // =====================================================

    @Transactional(readOnly = true)
    public List<VaccinationResponse> getAllVaccinations() {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return vaccinationRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET VACCINATION BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public VaccinationResponse getVaccinationById(Long id) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        Vaccination vaccination =
                vaccinationRepository.findByIdAndDoctorId(id, doctorId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Vaccination not found with id: "
                                                + id
                                )
                        );

        return mapToResponse(vaccination);
    }

    // =====================================================
    // GET VACCINATIONS BY PATIENT
    // =====================================================

    @Transactional(readOnly = true)
    public List<VaccinationResponse> getByPatient(
            Long patientId) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        if (!patientRepository.existsByIdAndDoctorId(patientId, doctorId)) {
            throw new RuntimeException(
                    "Patient not found with id: " + patientId
            );
        }

        return vaccinationRepository
                .findByPatientIdAndDoctorId(patientId, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY STATUS
    // =====================================================

    @Transactional(readOnly = true)
    public List<VaccinationResponse> getByStatus(
            String status) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return vaccinationRepository
                .findByStatusAndDoctorId(status, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY VACCINE NAME
    // =====================================================

    @Transactional(readOnly = true)
    public List<VaccinationResponse> getByVaccineName(
            String vaccineName) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return vaccinationRepository
                .findByVaccineNameAndDoctorId(vaccineName, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET UPCOMING / DUE VACCINATIONS
    // =====================================================

    @Transactional(readOnly = true)
    public List<VaccinationResponse> getDueVaccinations(
            LocalDate date) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return vaccinationRepository
                .findByNextDueDateBeforeAndDoctorId(date.plusDays(1), doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET VACCINATIONS BETWEEN DATES
    // =====================================================

    @Transactional(readOnly = true)
    public List<VaccinationResponse>
    getVaccinationsBetweenDates(
            LocalDate startDate,
            LocalDate endDate) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return vaccinationRepository
                .findByNextDueDateBetweenAndDoctorId(
                        startDate,
                        endDate,
                        doctorId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET PATIENT VACCINATIONS BY STATUS
    // =====================================================

    @Transactional(readOnly = true)
    public List<VaccinationResponse>
    getPatientVaccinationsByStatus(
            Long patientId,
            String status) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        if (!patientRepository.existsByIdAndDoctorId(patientId, doctorId)) {
            throw new RuntimeException(
                    "Patient not found with id: " + patientId
            );
        }

        return vaccinationRepository
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
    // GET VACCINATIONS ON DATE
    // =====================================================

    @Transactional(readOnly = true)
    public List<VaccinationResponse>
    getVaccinationsOnDate(
            LocalDate vaccinationDate) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return vaccinationRepository
                .findByVaccinationDateAndDoctorId(vaccinationDate, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // UPDATE VACCINATION
    // =====================================================

    public VaccinationResponse updateVaccination(
            Long id,
            VaccinationRequest request) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        Vaccination vaccination =
                vaccinationRepository.findByIdAndDoctorId(id, doctorId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Vaccination not found with id: "
                                                + id
                                )
                        );

        if (request.getPatientId() != null) {

            Patient patient =
                    patientRepository.findByIdAndDoctorId(
                            request.getPatientId(),
                            doctorId
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "Patient not found with id: "
                                            + request.getPatientId()
                            )
                    );

            vaccination.setPatient(patient);
        }

        vaccination.setDoctorId(doctorId);

        if (request.getVaccineName() != null &&
                !request.getVaccineName().trim().isEmpty()) {

            vaccination.setVaccineName(
                    request.getVaccineName()
            );
        }

        if (request.getVaccineType() != null) {

            vaccination.setVaccineType(
                    request.getVaccineType()
            );
        }

        if (request.getManufacturer() != null) {

            vaccination.setManufacturer(
                    request.getManufacturer()
            );
        }

        if (request.getBatchNumber() != null) {

            vaccination.setBatchNumber(
                    request.getBatchNumber()
            );
        }

        if (request.getVaccinationDate() != null) {

            vaccination.setVaccinationDate(
                    request.getVaccinationDate()
            );
        }

        if (request.getNextDueDate() != null) {

            vaccination.setNextDueDate(
                    request.getNextDueDate()
            );
        }

        if (request.getDosage() != null) {

            vaccination.setDosage(
                    request.getDosage()
            );
        }

        if (request.getRoute() != null) {

            vaccination.setRoute(
                    request.getRoute()
            );
        }

        if (request.getAdministeredBy() != null) {

            vaccination.setAdministeredBy(
                    request.getAdministeredBy()
            );
        }

        if (request.getStatus() != null &&
                !request.getStatus().trim().isEmpty()) {

            vaccination.setStatus(
                    request.getStatus()
            );
        }

        if (request.getNotes() != null) {

            vaccination.setNotes(
                    request.getNotes()
            );
        }

        Vaccination updatedVaccination =
                vaccinationRepository.save(vaccination);

        return mapToResponse(updatedVaccination);
    }

    // =====================================================
    // DELETE VACCINATION
    // =====================================================

    public void deleteVaccination(Long id) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        Vaccination vaccination = vaccinationRepository.findByIdAndDoctorId(id, doctorId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Vaccination not found with id: "
                                        + id
                        )
                );

        vaccinationRepository.delete(vaccination);
    }

    // =====================================================
    // ENTITY → RESPONSE
    // =====================================================

    private VaccinationResponse mapToResponse(
            Vaccination vaccination) {

        VaccinationResponse response =
                new VaccinationResponse();

        response.setId(vaccination.getId());
        response.setDoctorId(vaccination.getDoctorId());

        if (vaccination.getPatient() != null) {

            response.setPatientId(
                    vaccination.getPatient().getId()
            );

            response.setPatientName(
                    vaccination.getPatient().getName()
            );
        }

        response.setVaccineName(
                vaccination.getVaccineName()
        );

        response.setVaccineType(
                vaccination.getVaccineType()
        );

        response.setManufacturer(
                vaccination.getManufacturer()
        );

        response.setBatchNumber(
                vaccination.getBatchNumber()
        );

        response.setVaccinationDate(
                vaccination.getVaccinationDate()
        );

        response.setNextDueDate(
                vaccination.getNextDueDate()
        );

        response.setDosage(
                vaccination.getDosage()
        );

        response.setRoute(
                vaccination.getRoute()
        );

        response.setAdministeredBy(
                vaccination.getAdministeredBy()
        );

        response.setStatus(
                vaccination.getStatus()
        );

        response.setNotes(
                vaccination.getNotes()
        );

        response.setCreatedAt(
                vaccination.getCreatedAt()
        );

        response.setUpdatedAt(
                vaccination.getUpdatedAt()
        );

        return response;
    }
}