package doctor.backend.controller;

import doctor.backend.dto.vaccination.VaccinationRequest;
import doctor.backend.dto.vaccination.VaccinationResponse;
import doctor.backend.service.VaccinationService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/vaccinations")
@CrossOrigin(origins = "*")
public class VaccinationController {

    private final VaccinationService vaccinationService;

    public VaccinationController(
            VaccinationService vaccinationService) {

        this.vaccinationService = vaccinationService;
    }

    // =====================================================
    // CREATE VACCINATION
    // POST /api/vaccinations
    // =====================================================

    @PostMapping
    public ResponseEntity<VaccinationResponse>
    createVaccination(
            @RequestBody VaccinationRequest request) {

        VaccinationResponse response =
                vaccinationService.createVaccination(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    // =====================================================
    // GET ALL VACCINATIONS
    // GET /api/vaccinations
    // =====================================================

    @GetMapping
    public ResponseEntity<List<VaccinationResponse>>
    getAllVaccinations() {

        return ResponseEntity.ok(
                vaccinationService.getAllVaccinations()
        );
    }

    // =====================================================
    // GET VACCINATION BY ID
    // GET /api/vaccinations/{id}
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<VaccinationResponse>
    getVaccinationById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                vaccinationService.getVaccinationById(id)
        );
    }

    // =====================================================
    // GET VACCINATIONS BY PATIENT
    // GET /api/vaccinations/patient/{patientId}
    // =====================================================

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<VaccinationResponse>>
    getByPatient(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                vaccinationService.getByPatient(patientId)
        );
    }

    // =====================================================
    // GET VACCINATIONS BY STATUS
    // GET /api/vaccinations/status/{status}
    // =====================================================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<VaccinationResponse>>
    getByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                vaccinationService.getByStatus(status)
        );
    }

    // =====================================================
    // GET VACCINATIONS BY VACCINE NAME
    // GET /api/vaccinations/vaccine/{vaccineName}
    // =====================================================

    @GetMapping("/vaccine/{vaccineName}")
    public ResponseEntity<List<VaccinationResponse>>
    getByVaccineName(
            @PathVariable String vaccineName) {

        return ResponseEntity.ok(
                vaccinationService.getByVaccineName(vaccineName)
        );
    }

    // =====================================================
    // GET DUE VACCINATIONS
    // GET /api/vaccinations/due?date=2026-12-31
    // =====================================================

    @GetMapping("/due")
    public ResponseEntity<List<VaccinationResponse>>
    getDueVaccinations(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        return ResponseEntity.ok(
                vaccinationService.getDueVaccinations(date)
        );
    }

    // =====================================================
    // GET VACCINATIONS BETWEEN DATES
    // GET /api/vaccinations/date-range
    // =====================================================

    @GetMapping("/date-range")
    public ResponseEntity<List<VaccinationResponse>>
    getVaccinationsBetweenDates(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return ResponseEntity.ok(
                vaccinationService
                        .getVaccinationsBetweenDates(
                                startDate,
                                endDate
                        )
        );
    }

    // =====================================================
    // GET PATIENT VACCINATIONS BY STATUS
    // GET /api/vaccinations/patient/{patientId}/status/{status}
    // =====================================================

    @GetMapping("/patient/{patientId}/status/{status}")
    public ResponseEntity<List<VaccinationResponse>>
    getPatientVaccinationsByStatus(

            @PathVariable Long patientId,
            @PathVariable String status) {

        return ResponseEntity.ok(
                vaccinationService
                        .getPatientVaccinationsByStatus(
                                patientId,
                                status
                        )
        );
    }

    // =====================================================
    // GET VACCINATIONS ON PARTICULAR DATE
    // GET /api/vaccinations/date?date=2026-08-12
    // =====================================================

    @GetMapping("/date")
    public ResponseEntity<List<VaccinationResponse>>
    getVaccinationsOnDate(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        return ResponseEntity.ok(
                vaccinationService
                        .getVaccinationsOnDate(date)
        );
    }

    // =====================================================
    // UPDATE VACCINATION
    // PUT /api/vaccinations/{id}
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<VaccinationResponse>
    updateVaccination(

            @PathVariable Long id,
            @RequestBody VaccinationRequest request) {

        return ResponseEntity.ok(
                vaccinationService.updateVaccination(
                        id,
                        request
                )
        );
    }

    // =====================================================
    // DELETE VACCINATION
    // DELETE /api/vaccinations/{id}
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deleteVaccination(
            @PathVariable Long id) {

        vaccinationService.deleteVaccination(id);

        return ResponseEntity.noContent().build();
    }
}