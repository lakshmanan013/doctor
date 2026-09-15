package doctor.backend.service;

import doctor.backend.dto.followup.FollowUpRequest;
import doctor.backend.dto.invoice.InvoiceRequest;
import doctor.backend.dto.prescription.PrescriptionItemRequest;
import doctor.backend.dto.prescription.PrescriptionItemResponse;
import doctor.backend.dto.prescription.PrescriptionRequest;
import doctor.backend.dto.prescription.PrescriptionResponse;
import doctor.backend.entity.Medicine;
import doctor.backend.entity.MedicalRecord;
import doctor.backend.entity.Owner;
import doctor.backend.entity.Patient;
import doctor.backend.entity.Prescription;
import doctor.backend.entity.PrescriptionItem;
import doctor.backend.repository.MedicalRecordRepository;
import doctor.backend.repository.MedicineRepository;
import doctor.backend.repository.PatientRepository;
import doctor.backend.repository.PrescriptionRepository;
import doctor.backend.security.CurrentUserProvider;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PrescriptionService {

    // Default gap (in days) before a routine follow-up is due after a
    // prescription is written. Kept as a single constant so the business
    // rule is easy to find/change later (e.g. if it should vary by diagnosis).
    private static final int DEFAULT_FOLLOW_UP_GAP_DAYS = 7;

    // Default invoice due date, in days from the invoice date.
    private static final int DEFAULT_INVOICE_DUE_DAYS = 7;

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicineRepository medicineRepository;
    private final FollowUpService followUpService;
    private final InvoiceService invoiceService;
    private final CurrentUserProvider currentUserProvider;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            PatientRepository patientRepository,
            MedicalRecordRepository medicalRecordRepository,
            MedicineRepository medicineRepository,
            FollowUpService followUpService,
            InvoiceService invoiceService,
            CurrentUserProvider currentUserProvider) {

        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository = patientRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.medicineRepository = medicineRepository;
        this.followUpService = followUpService;
        this.invoiceService = invoiceService;
        this.currentUserProvider = currentUserProvider;
    }

    // =====================================================
    // CREATE PRESCRIPTION
    // =====================================================

    public PrescriptionResponse createPrescription(
            PrescriptionRequest request) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        if (request.getPatientId() == null) {
            throw new RuntimeException("Patient ID is required");
        }

        if (request.getPrescriptionDate() == null) {
            throw new RuntimeException(
                    "Prescription date is required"
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

        Prescription prescription = new Prescription();

        prescription.setPatient(patient);
        prescription.setDoctorId(doctorId);
        prescription.setPrescriptionDate(
                request.getPrescriptionDate()
        );
        prescription.setDiagnosis(
                request.getDiagnosis()
        );
        prescription.setInstructions(
                request.getInstructions()
        );
        prescription.setNotes(
                request.getNotes()
        );
        prescription.setDoctorName(
                request.getDoctorName() != null && !request.getDoctorName().isBlank()
                        ? request.getDoctorName()
                        : currentUserProvider.getCurrentUser().getFullName()
        );

        // Optional medical record
        if (request.getMedicalRecordId() != null) {

            MedicalRecord medicalRecord =
                    medicalRecordRepository
                            .findByIdAndDoctorId(request.getMedicalRecordId(), doctorId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Medical record not found with id: "
                                                    + request.getMedicalRecordId()
                                    )
                            );

            if (!medicalRecord.getPatient()
                    .getId()
                    .equals(patient.getId())) {

                throw new RuntimeException(
                        "Medical record does not belong to this patient"
                );
            }

            prescription.setMedicalRecord(
                    medicalRecord
            );
        }

        // Add medicines
        if (request.getItems() != null) {

            for (PrescriptionItemRequest itemRequest
                    : request.getItems()) {

                PrescriptionItem item =
                        createPrescriptionItem(itemRequest);

                prescription.addItem(item);
            }
        }

        Prescription savedPrescription =
                prescriptionRepository.save(prescription);

        // A prescription is the trigger for both a routine follow-up
        // check-in and the bill for the medicines dispensed. Neither
        // should block the prescription itself from being saved if
        // something about the auto-generation is off, so failures here
        // are swallowed rather than propagated.
        createFollowUpForPrescription(savedPrescription);
        createInvoiceForPrescription(
                savedPrescription,
                request.getConsultationFee(),
                request.getFollowUpFee()
        );

        return mapToResponse(savedPrescription);
    }

    // =====================================================
    // AUTO-CREATE FOLLOW-UP FROM PRESCRIPTION
    // =====================================================

    private void createFollowUpForPrescription(
            Prescription prescription) {

        try {
            FollowUpRequest followUpRequest = new FollowUpRequest();

            followUpRequest.setPatientId(
                    prescription.getPatient().getId()
            );

            followUpRequest.setFollowUpDate(
                    prescription.getPrescriptionDate()
            );

            followUpRequest.setNextFollowUpDate(
                    prescription.getPrescriptionDate()
                            .plusDays(DEFAULT_FOLLOW_UP_GAP_DAYS)
            );

            followUpRequest.setReason(
                    prescription.getDiagnosis() != null
                            && !prescription.getDiagnosis().isBlank()
                            ? "Follow-up for: " + prescription.getDiagnosis()
                            : "Prescription follow-up"
            );

            followUpRequest.setDoctorName(
                    prescription.getDoctorName()
            );

            followUpRequest.setStatus("Scheduled");

            followUpService.createFollowUp(followUpRequest);

        } catch (Exception ex) {
            // Don't let a follow-up auto-creation issue block the
            // prescription save itself.
        }
    }

    // =====================================================
    // AUTO-CREATE INVOICE FROM PRESCRIPTION ITEMS
    // =====================================================

    private void createInvoiceForPrescription(
            Prescription prescription,
            BigDecimal consultationFee,
            BigDecimal followUpFee) {

        try {
            // --- Medicines dispensed ---
            BigDecimal medicineSubtotal = BigDecimal.ZERO;

            if (prescription.getItems() != null) {

                for (PrescriptionItem item : prescription.getItems()) {

                    Medicine medicine = item.getMedicine();

                    if (medicine == null || medicine.getPrice() == null) {
                        continue;
                    }

                    int quantity = parseQuantity(item.getQuantity());

                    medicineSubtotal = medicineSubtotal.add(
                            BigDecimal.valueOf(medicine.getPrice())
                                    .multiply(BigDecimal.valueOf(quantity))
                    );
                }
            }

            // --- Doctor fees for this visit ---
            BigDecimal fee = BigDecimal.ZERO;

            if (consultationFee != null && consultationFee.compareTo(BigDecimal.ZERO) > 0) {
                fee = fee.add(consultationFee);
            }

            if (followUpFee != null && followUpFee.compareTo(BigDecimal.ZERO) > 0) {
                fee = fee.add(followUpFee);
            }

            BigDecimal subtotal = medicineSubtotal.add(fee);

            if (subtotal.compareTo(BigDecimal.ZERO) <= 0) {
                // Nothing to bill - no priced items and no fee entered.
                return;
            }

            // A single combined invoice for the whole visit (medicines +
            // doctor fees), rather than one invoice per component - a
            // prescription should only ever produce one bill.
            InvoiceRequest invoiceRequest = new InvoiceRequest();

            invoiceRequest.setOwnerId(
                    prescription.getPatient().getOwner().getId()
            );

            invoiceRequest.setPatientId(
                    prescription.getPatient().getId()
            );

            invoiceRequest.setInvoiceDate(
                    prescription.getPrescriptionDate()
            );

            invoiceRequest.setDueDate(
                    prescription.getPrescriptionDate()
                            .plusDays(DEFAULT_INVOICE_DUE_DAYS)
            );

            invoiceRequest.setSubtotal(subtotal);
            invoiceRequest.setTax(BigDecimal.ZERO);
            invoiceRequest.setDiscount(BigDecimal.ZERO);
            invoiceRequest.setPaidAmount(BigDecimal.ZERO);

            StringBuilder notes = new StringBuilder(
                    "Auto-generated from prescription #"
                            + prescription.getId()
            );

            if (medicineSubtotal.compareTo(BigDecimal.ZERO) > 0) {
                notes.append(" | Medicines: \u20B9").append(medicineSubtotal);
            }

            if (consultationFee != null && consultationFee.compareTo(BigDecimal.ZERO) > 0) {
                notes.append(" | Consultation fee: \u20B9").append(consultationFee);
            }

            if (followUpFee != null && followUpFee.compareTo(BigDecimal.ZERO) > 0) {
                notes.append(" | Follow-up fee: \u20B9").append(followUpFee);
            }

            invoiceRequest.setNotes(notes.toString());

            invoiceService.createInvoice(invoiceRequest);

        } catch (Exception ex) {
            // Don't let invoice auto-generation block the prescription
            // save itself (e.g. a patient with no linked owner yet).
        }
    }

    // =====================================================
    // PARSE FREE-TEXT QUANTITY (e.g. "10 tablets") → INT
    // =====================================================

    private int parseQuantity(String quantity) {

        if (quantity == null || quantity.isBlank()) {
            return 1;
        }

        StringBuilder digits = new StringBuilder();

        for (char c : quantity.trim().toCharArray()) {
            if (Character.isDigit(c)) {
                digits.append(c);
            } else if (digits.length() > 0) {
                break;
            }
        }

        if (digits.length() == 0) {
            return 1;
        }

        try {
            return Math.max(1, Integer.parseInt(digits.toString()));
        } catch (NumberFormatException ex) {
            return 1;
        }
    }

    // =====================================================
    // CREATE PRESCRIPTION ITEM
    // =====================================================

    private PrescriptionItem createPrescriptionItem(
            PrescriptionItemRequest request) {

        if (request.getMedicineId() == null) {
            throw new RuntimeException(
                    "Medicine ID is required"
            );
        }

        Medicine medicine = medicineRepository
                .findById(request.getMedicineId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Medicine not found with id: "
                                        + request.getMedicineId()
                        )
                );

        PrescriptionItem item =
                new PrescriptionItem();

        item.setMedicine(medicine);
        item.setDosage(request.getDosage());
        item.setFrequency(request.getFrequency());
        item.setDuration(request.getDuration());
        item.setRoute(request.getRoute());
        item.setQuantity(request.getQuantity());
        item.setInstructions(
                request.getInstructions()
        );

        return item;
    }

    // =====================================================
    // GET ALL PRESCRIPTIONS
    // =====================================================

    @Transactional(readOnly = true)
    public List<PrescriptionResponse>
    getAllPrescriptions() {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return prescriptionRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET PRESCRIPTION BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionById(
            Long id) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        Prescription prescription =
                prescriptionRepository.findByIdAndDoctorId(id, doctorId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Prescription not found with id: "
                                                + id
                                )
                        );

        return mapToResponse(prescription);
    }

    // =====================================================
    // GET PRESCRIPTIONS BY PATIENT
    // =====================================================

    @Transactional(readOnly = true)
    public List<PrescriptionResponse>
    getPrescriptionsByPatient(Long patientId) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        if (!patientRepository.existsByIdAndDoctorId(patientId, doctorId)) {
            throw new RuntimeException(
                    "Patient not found with id: " + patientId
            );
        }

        return prescriptionRepository
                .findByPatientIdAndDoctorId(patientId, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET PRESCRIPTIONS BY MEDICAL RECORD
    // =====================================================

    @Transactional(readOnly = true)
    public List<PrescriptionResponse>
    getPrescriptionsByMedicalRecord(
            Long medicalRecordId) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        if (medicalRecordRepository
                .findByIdAndDoctorId(medicalRecordId, doctorId).isEmpty()) {

            throw new RuntimeException(
                    "Medical record not found with id: "
                            + medicalRecordId
            );
        }

        return prescriptionRepository
                .findByMedicalRecordIdAndDoctorId(medicalRecordId, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET PRESCRIPTIONS BY DATE
    // =====================================================

    @Transactional(readOnly = true)
    public List<PrescriptionResponse>
    getPrescriptionsByDate(LocalDate date) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return prescriptionRepository
                .findByPrescriptionDateAndDoctorId(date, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET PATIENT PRESCRIPTIONS BY DATE
    // =====================================================

    @Transactional(readOnly = true)
    public List<PrescriptionResponse>
    getPrescriptionsByPatientAndDate(
            Long patientId,
            LocalDate date) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        if (!patientRepository.existsByIdAndDoctorId(patientId, doctorId)) {
            throw new RuntimeException(
                    "Patient not found with id: " + patientId
            );
        }

        return prescriptionRepository
                .findByPatientIdAndPrescriptionDateAndDoctorId(
                        patientId,
                        date,
                        doctorId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET PRESCRIPTIONS BETWEEN DATES
    // =====================================================

    @Transactional(readOnly = true)
    public List<PrescriptionResponse>
    getPrescriptionsBetweenDates(
            LocalDate startDate,
            LocalDate endDate) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return prescriptionRepository
                .findByPrescriptionDateBetweenAndDoctorId(
                        startDate,
                        endDate,
                        doctorId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // UPDATE PRESCRIPTION
    // =====================================================

    public PrescriptionResponse updatePrescription(
            Long id,
            PrescriptionRequest request) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        Prescription prescription =
                prescriptionRepository.findByIdAndDoctorId(id, doctorId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Prescription not found with id: "
                                                + id
                                )
                        );

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

        prescription.setPatient(patient);
        prescription.setDoctorId(doctorId);

        prescription.setPrescriptionDate(
                request.getPrescriptionDate()
        );

        prescription.setDiagnosis(
                request.getDiagnosis()
        );

        prescription.setInstructions(
                request.getInstructions()
        );

        prescription.setNotes(
                request.getNotes()
        );

        if (request.getDoctorName() != null && !request.getDoctorName().isBlank()) {
            prescription.setDoctorName(request.getDoctorName());
        }

        // Update medical record
        prescription.setMedicalRecord(null);

        if (request.getMedicalRecordId() != null) {

            MedicalRecord medicalRecord =
                    medicalRecordRepository
                            .findByIdAndDoctorId(request.getMedicalRecordId(), doctorId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Medical record not found with id: "
                                                    + request.getMedicalRecordId()
                                    )
                            );

            if (!medicalRecord.getPatient()
                    .getId()
                    .equals(patient.getId())) {

                throw new RuntimeException(
                        "Medical record does not belong to this patient"
                );
            }

            prescription.setMedicalRecord(
                    medicalRecord
            );
        }

        // Replace prescription items
        prescription.getItems().clear();

        if (request.getItems() != null) {

            for (PrescriptionItemRequest itemRequest
                    : request.getItems()) {

                PrescriptionItem item =
                        createPrescriptionItem(itemRequest);

                prescription.addItem(item);
            }
        }

        Prescription updatedPrescription =
                prescriptionRepository.save(prescription);

        return mapToResponse(updatedPrescription);
    }

    // =====================================================
    // DELETE PRESCRIPTION
    // =====================================================

    public void deletePrescription(Long id) {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        Prescription prescription = prescriptionRepository.findByIdAndDoctorId(id, doctorId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Prescription not found with id: " + id
                        )
                );

        prescriptionRepository.delete(prescription);
    }

    // =====================================================
    // ENTITY → RESPONSE
    // =====================================================

    private PrescriptionResponse mapToResponse(
            Prescription prescription) {

        PrescriptionResponse response =
                new PrescriptionResponse();

        response.setId(prescription.getId());

        response.setPrescriptionDate(
                prescription.getPrescriptionDate()
        );

        response.setDiagnosis(
                prescription.getDiagnosis()
        );

        response.setInstructions(
                prescription.getInstructions()
        );

        response.setNotes(
                prescription.getNotes()
        );

        response.setDoctorName(
                prescription.getDoctorName()
        );

        response.setDoctorId(
                prescription.getDoctorId()
        );

        response.setCreatedAt(
                prescription.getCreatedAt()
        );

        response.setUpdatedAt(
                prescription.getUpdatedAt()
        );

        // =========================
        // Patient
        // =========================

        Patient patient =
                prescription.getPatient();

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

            response.setGender(
                    patient.getGender()
            );

            response.setIcon(
                    patient.getIcon()
            );

            // =========================
            // Owner
            // =========================

            Owner owner =
                    patient.getOwner();

            if (owner != null) {

                response.setOwnerId(
                        owner.getId()
                );

                response.setOwnerName(
                        owner.getFullName()
                );

                response.setOwnerPhone(
                        owner.getPhone()
                );
            }
        }

        // =========================
        // Medical Record
        // =========================

        MedicalRecord medicalRecord =
                prescription.getMedicalRecord();

        if (medicalRecord != null) {

            response.setMedicalRecordId(
                    medicalRecord.getId()
            );
        }

        // =========================
        // Items
        // =========================

        List<PrescriptionItemResponse> itemResponses =
                new ArrayList<>();

        if (prescription.getItems() != null) {

            for (PrescriptionItem item
                    : prescription.getItems()) {

                PrescriptionItemResponse itemResponse =
                        new PrescriptionItemResponse();

                itemResponse.setId(
                        item.getId()
                );

                Medicine medicine =
                        item.getMedicine();

                if (medicine != null) {

                    itemResponse.setMedicineId(
                            medicine.getId()
                    );

                    itemResponse.setMedicineName(
                            medicine.getName()
                    );
                }

                itemResponse.setDosage(
                        item.getDosage()
                );

                itemResponse.setFrequency(
                        item.getFrequency()
                );

                itemResponse.setDuration(
                        item.getDuration()
                );

                itemResponse.setRoute(
                        item.getRoute()
                );

                itemResponse.setQuantity(
                        item.getQuantity()
                );

                itemResponse.setInstructions(
                        item.getInstructions()
                );

                itemResponses.add(itemResponse);
            }
        }

        response.setItems(itemResponses);

        return response;
    }
}