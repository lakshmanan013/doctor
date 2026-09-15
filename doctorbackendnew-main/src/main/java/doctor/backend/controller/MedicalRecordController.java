package doctor.backend.controller;

import doctor.backend.dto.medicalrecord.MedicalRecordRequest;
import doctor.backend.dto.medicalrecord.MedicalRecordResponse;
import doctor.backend.service.MedicalRecordService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@CrossOrigin(origins = "*")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(
            MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    // =====================================================
    // CREATE MEDICAL RECORD
    // POST /api/medical-records
    // =====================================================

    @PostMapping
    public ResponseEntity<MedicalRecordResponse> createMedicalRecord(
            @RequestBody MedicalRecordRequest request) {

        MedicalRecordResponse response =
                medicalRecordService.createMedicalRecord(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    // =====================================================
    // GET ALL MEDICAL RECORDS
    // GET /api/medical-records
    // =====================================================

    @GetMapping
    public ResponseEntity<List<MedicalRecordResponse>>
    getAllMedicalRecords() {

        return ResponseEntity.ok(
                medicalRecordService.getAllMedicalRecords()
        );
    }

    // =====================================================
    // GET MEDICAL RECORD BY ID
    // GET /api/medical-records/{id}
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponse>
    getMedicalRecordById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                medicalRecordService.getMedicalRecordById(id)
        );
    }

    // =====================================================
    // GET RECORDS BY PATIENT
    // GET /api/medical-records/patient/{patientId}
    // =====================================================

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecordResponse>>
    getRecordsByPatient(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                medicalRecordService
                        .getRecordsByPatient(patientId)
        );
    }

    // =====================================================
    // GET RECORDS BY DATE
    // GET /api/medical-records/date/{date}
    // =====================================================

    @GetMapping("/date/{date}")
    public ResponseEntity<List<MedicalRecordResponse>>
    getRecordsByDate(
            @PathVariable LocalDate date) {

        return ResponseEntity.ok(
                medicalRecordService
                        .getRecordsByDate(date)
        );
    }

    // =====================================================
    // GET PATIENT RECORDS BY DATE
    // GET /api/medical-records/patient/{patientId}/date/{date}
    // =====================================================

    @GetMapping("/patient/{patientId}/date/{date}")
    public ResponseEntity<List<MedicalRecordResponse>>
    getRecordsByPatientAndDate(
            @PathVariable Long patientId,
            @PathVariable LocalDate date) {

        return ResponseEntity.ok(
                medicalRecordService
                        .getRecordsByPatientAndDate(
                                patientId,
                                date
                        )
        );
    }

    // =====================================================
    // GET RECORDS BETWEEN DATES
    // GET /api/medical-records/range
    // =====================================================

    @GetMapping("/range")
    public ResponseEntity<List<MedicalRecordResponse>>
    getRecordsBetweenDates(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return ResponseEntity.ok(
                medicalRecordService
                        .getRecordsBetweenDates(
                                startDate,
                                endDate
                        )
        );
    }

    // =====================================================
    // UPDATE MEDICAL RECORD
    // PUT /api/medical-records/{id}
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordResponse>
    updateMedicalRecord(
            @PathVariable Long id,
            @RequestBody MedicalRecordRequest request) {

        return ResponseEntity.ok(
                medicalRecordService
                        .updateMedicalRecord(id, request)
        );
    }

    // =====================================================
    // DELETE MEDICAL RECORD
    // DELETE /api/medical-records/{id}
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicalRecord(
            @PathVariable Long id) {

        medicalRecordService.deleteMedicalRecord(id);

        return ResponseEntity.noContent().build();
    }
}