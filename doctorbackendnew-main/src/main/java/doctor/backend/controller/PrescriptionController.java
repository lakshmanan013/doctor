package doctor.backend.controller;

import doctor.backend.dto.prescription.PrescriptionRequest;
import doctor.backend.dto.prescription.PrescriptionResponse;
import doctor.backend.service.PrescriptionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "*")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(
            PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    // =====================================================
    // CREATE PRESCRIPTION
    // POST /api/prescriptions
    // =====================================================

    @PostMapping
    public ResponseEntity<PrescriptionResponse> createPrescription(
            @RequestBody PrescriptionRequest request) {

        PrescriptionResponse response =
                prescriptionService.createPrescription(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    // =====================================================
    // GET ALL PRESCRIPTIONS
    // GET /api/prescriptions
    // =====================================================

    @GetMapping
    public ResponseEntity<List<PrescriptionResponse>>
    getAllPrescriptions() {

        return ResponseEntity.ok(
                prescriptionService.getAllPrescriptions()
        );
    }

    // =====================================================
    // GET PRESCRIPTION BY ID
    // GET /api/prescriptions/{id}
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionResponse>
    getPrescriptionById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                prescriptionService.getPrescriptionById(id)
        );
    }

    // =====================================================
    // GET PRESCRIPTIONS BY PATIENT
    // GET /api/prescriptions/patient/{patientId}
    // =====================================================

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionResponse>>
    getPrescriptionsByPatient(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                prescriptionService
                        .getPrescriptionsByPatient(patientId)
        );
    }

    // =====================================================
    // GET PRESCRIPTIONS BY MEDICAL RECORD
    // GET /api/prescriptions/medical-record/{medicalRecordId}
    // =====================================================

    @GetMapping("/medical-record/{medicalRecordId}")
    public ResponseEntity<List<PrescriptionResponse>>
    getPrescriptionsByMedicalRecord(
            @PathVariable Long medicalRecordId) {

        return ResponseEntity.ok(
                prescriptionService
                        .getPrescriptionsByMedicalRecord(
                                medicalRecordId
                        )
        );
    }

    // =====================================================
    // GET PRESCRIPTIONS BY DATE
    // GET /api/prescriptions/date/{date}
    // =====================================================

    @GetMapping("/date/{date}")
    public ResponseEntity<List<PrescriptionResponse>>
    getPrescriptionsByDate(
            @PathVariable LocalDate date) {

        return ResponseEntity.ok(
                prescriptionService
                        .getPrescriptionsByDate(date)
        );
    }

    // =====================================================
    // GET PATIENT PRESCRIPTIONS BY DATE
    // GET /api/prescriptions/patient/{patientId}/date/{date}
    // =====================================================

    @GetMapping("/patient/{patientId}/date/{date}")
    public ResponseEntity<List<PrescriptionResponse>>
    getPrescriptionsByPatientAndDate(
            @PathVariable Long patientId,
            @PathVariable LocalDate date) {

        return ResponseEntity.ok(
                prescriptionService
                        .getPrescriptionsByPatientAndDate(
                                patientId,
                                date
                        )
        );
    }

    // =====================================================
    // GET PRESCRIPTIONS BETWEEN DATES
    // GET /api/prescriptions/range
    // =====================================================

    @GetMapping("/range")
    public ResponseEntity<List<PrescriptionResponse>>
    getPrescriptionsBetweenDates(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return ResponseEntity.ok(
                prescriptionService
                        .getPrescriptionsBetweenDates(
                                startDate,
                                endDate
                        )
        );
    }

    // =====================================================
    // UPDATE PRESCRIPTION
    // PUT /api/prescriptions/{id}
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionResponse>
    updatePrescription(
            @PathVariable Long id,
            @RequestBody PrescriptionRequest request) {

        return ResponseEntity.ok(
                prescriptionService
                        .updatePrescription(id, request)
        );
    }

    // =====================================================
    // DELETE PRESCRIPTION
    // DELETE /api/prescriptions/{id}
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescription(
            @PathVariable Long id) {

        prescriptionService.deletePrescription(id);

        return ResponseEntity.noContent().build();
    }
}