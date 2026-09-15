package doctor.backend.service;

import doctor.backend.dto.medicalrecord.MedicalRecordRequest;
import doctor.backend.dto.medicalrecord.MedicalRecordResponse;
import doctor.backend.entity.MedicalRecord;
import doctor.backend.entity.Owner;
import doctor.backend.entity.Patient;
import doctor.backend.repository.MedicalRecordRepository;
import doctor.backend.repository.PatientRepository;
import doctor.backend.security.CurrentUserProvider;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final CurrentUserProvider currentUserProvider;

    public MedicalRecordService(
            MedicalRecordRepository medicalRecordRepository,
            PatientRepository patientRepository,
            CurrentUserProvider currentUserProvider) {

        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
        this.currentUserProvider = currentUserProvider;
    }

    // =====================================================
    // CREATE MEDICAL RECORD
    // =====================================================

    public MedicalRecordResponse createMedicalRecord(
            MedicalRecordRequest request) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        Patient patient = patientRepository.findByIdAndDoctorId(request.getPatientId(), doctorId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Patient not found with id: "
                                        + request.getPatientId()
                        )
                );

        MedicalRecord record = new MedicalRecord();

        record.setPatient(patient);
        record.setDoctorId(doctorId);
        record.setVisitDate(request.getVisitDate());
        record.setChiefComplaint(request.getChiefComplaint());
        record.setSymptoms(request.getSymptoms());
        record.setDiagnosis(request.getDiagnosis());
        record.setClinicalFindings(request.getClinicalFindings());
        record.setTreatment(request.getTreatment());
        record.setWeight(request.getWeight());
        record.setTemperature(request.getTemperature());
        record.setNotes(request.getNotes());
        record.setDoctorName(request.getDoctorName() != null ? request.getDoctorName() : currentUserProvider.getCurrentUser().getFullName());

        MedicalRecord savedRecord =
                medicalRecordRepository.save(record);

        return mapToResponse(savedRecord);
    }

    // =====================================================
    // GET ALL MEDICAL RECORDS
    // =====================================================

    public List<MedicalRecordResponse> getAllMedicalRecords() {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return medicalRecordRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET MEDICAL RECORD BY ID
    // =====================================================

    public MedicalRecordResponse getMedicalRecordById(Long id) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        MedicalRecord record =
                medicalRecordRepository.findByIdAndDoctorId(id, doctorId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Medical record not found with id: "
                                                + id
                                )
                        );

        return mapToResponse(record);
    }

    // =====================================================
    // GET RECORDS BY PATIENT
    // =====================================================

    public List<MedicalRecordResponse> getRecordsByPatient(
            Long patientId) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        if (!patientRepository.existsByIdAndDoctorId(patientId, doctorId)) {
            throw new RuntimeException(
                    "Patient not found with id: " + patientId
            );
        }

        return medicalRecordRepository
                .findByPatientIdAndDoctorId(patientId, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET RECORDS BY DATE
    // =====================================================

    public List<MedicalRecordResponse> getRecordsByDate(
            LocalDate date) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return medicalRecordRepository
                .findByVisitDateAndDoctorId(date, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET PATIENT RECORDS BY DATE
    // =====================================================

    public List<MedicalRecordResponse> getRecordsByPatientAndDate(
            Long patientId,
            LocalDate date) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        if (!patientRepository.existsByIdAndDoctorId(patientId, doctorId)) {
            throw new RuntimeException(
                    "Patient not found with id: " + patientId
            );
        }

        return medicalRecordRepository
                .findByPatientIdAndVisitDateAndDoctorId(
                        patientId,
                        date,
                        doctorId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET RECORDS BETWEEN DATES
    // =====================================================

    public List<MedicalRecordResponse> getRecordsBetweenDates(
            LocalDate startDate,
            LocalDate endDate) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return medicalRecordRepository
                .findByVisitDateBetweenAndDoctorId(
                        startDate,
                        endDate,
                        doctorId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // UPDATE MEDICAL RECORD
    // =====================================================

    public MedicalRecordResponse updateMedicalRecord(
            Long id,
            MedicalRecordRequest request) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        MedicalRecord record =
                medicalRecordRepository.findByIdAndDoctorId(id, doctorId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Medical record not found with id: "
                                                + id
                                )
                        );

        Patient patient =
                patientRepository.findByIdAndDoctorId(request.getPatientId(), doctorId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Patient not found with id: "
                                                + request.getPatientId()
                                )
                        );

        record.setPatient(patient);
        record.setDoctorId(doctorId);
        record.setVisitDate(request.getVisitDate());
        record.setChiefComplaint(request.getChiefComplaint());
        record.setSymptoms(request.getSymptoms());
        record.setDiagnosis(request.getDiagnosis());
        record.setClinicalFindings(request.getClinicalFindings());
        record.setTreatment(request.getTreatment());
        record.setWeight(request.getWeight());
        record.setTemperature(request.getTemperature());
        record.setNotes(request.getNotes());
        if (request.getDoctorName() != null && !request.getDoctorName().isBlank()) {
            record.setDoctorName(request.getDoctorName());
        }

        MedicalRecord updatedRecord =
                medicalRecordRepository.save(record);

        return mapToResponse(updatedRecord);
    }

    // =====================================================
    // DELETE MEDICAL RECORD
    // =====================================================

    public void deleteMedicalRecord(Long id) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        MedicalRecord record = medicalRecordRepository.findByIdAndDoctorId(id, doctorId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Medical record not found with id: " + id
                        )
                );

        medicalRecordRepository.delete(record);
    }

    // =====================================================
    // ENTITY → RESPONSE DTO
    // =====================================================

    private MedicalRecordResponse mapToResponse(
            MedicalRecord record) {

        MedicalRecordResponse response =
                new MedicalRecordResponse();

        Patient patient = record.getPatient();

        // Record information
        response.setId(record.getId());
        response.setDoctorId(record.getDoctorId());
        response.setVisitDate(record.getVisitDate());
        response.setChiefComplaint(
                record.getChiefComplaint()
        );
        response.setSymptoms(record.getSymptoms());
        response.setDiagnosis(record.getDiagnosis());
        response.setClinicalFindings(
                record.getClinicalFindings()
        );
        response.setTreatment(record.getTreatment());
        response.setWeight(record.getWeight());
        response.setTemperature(record.getTemperature());
        response.setNotes(record.getNotes());
        response.setDoctorName(record.getDoctorName());
        response.setCreatedAt(record.getCreatedAt());
        response.setUpdatedAt(record.getUpdatedAt());

        // Patient information
        if (patient != null) {

            response.setPatientId(patient.getId());
            response.setPatientName(patient.getName());
            response.setSpecies(patient.getSpecies());
            response.setBreed(patient.getBreed());
            response.setGender(patient.getGender());
            response.setIcon(patient.getIcon());

            // Owner information
            Owner owner = patient.getOwner();

            if (owner != null) {
                response.setOwnerId(owner.getId());
                response.setOwnerName(owner.getFullName());
                response.setOwnerPhone(owner.getPhone());
            }
        }

        return response;
    }
}