package doctor.backend.controller;

import doctor.backend.dto.patient.PatientRequest;
import doctor.backend.dto.patient.PatientResponse;
import doctor.backend.service.PatientService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "http://localhost:5173")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // =====================================================
    // CREATE PATIENT
    // POST /api/patients
    // =====================================================

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(
            @Valid @RequestBody PatientRequest request
    ) {

        PatientResponse response =
                patientService.createPatient(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =====================================================
    // GET ALL PATIENTS
    // GET /api/patients
    // =====================================================

    @GetMapping
    public ResponseEntity<List<PatientResponse>> getAllPatients() {

        return ResponseEntity.ok(
                patientService.getAllPatients()
        );
    }

    // =====================================================
    // SEARCH PATIENTS
    // GET /api/patients/search?name=Bruno
    // =====================================================

    @GetMapping("/search")
    public ResponseEntity<List<PatientResponse>> searchPatients(
            @RequestParam String name
    ) {

        return ResponseEntity.ok(
                patientService.searchPatients(name)
        );
    }

    // =====================================================
    // GET PATIENTS BY OWNER
    // GET /api/patients/owner/{ownerId}
    // =====================================================

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<PatientResponse>> getPatientsByOwner(
            @PathVariable Long ownerId
    ) {

        return ResponseEntity.ok(
                patientService.getPatientsByOwner(ownerId)
        );
    }

    // =====================================================
    // GET PATIENTS BY SPECIES
    // GET /api/patients/species/{species}
    // =====================================================

    @GetMapping("/species/{species}")
    public ResponseEntity<List<PatientResponse>> getPatientsBySpecies(
            @PathVariable String species
    ) {

        return ResponseEntity.ok(
                patientService.getPatientsBySpecies(species)
        );
    }

    // =====================================================
    // GET PATIENTS BY STATUS
    // GET /api/patients/status/{status}
    // =====================================================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PatientResponse>> getPatientsByStatus(
            @PathVariable String status
    ) {

        return ResponseEntity.ok(
                patientService.getPatientsByStatus(status)
        );
    }

    // =====================================================
    // GET PATIENT BY DATABASE ID
    // GET /api/patients/{id}
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                patientService.getPatientById(id)
        );
    }

    // =====================================================
    // UPDATE PATIENT
    // PUT /api/patients/{id}
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequest request
    ) {

        return ResponseEntity.ok(
                patientService.updatePatient(id, request)
        );
    }

    // =====================================================
    // DELETE PATIENT
    // DELETE /api/patients/{id}
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(
            @PathVariable Long id
    ) {

        patientService.deletePatient(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}