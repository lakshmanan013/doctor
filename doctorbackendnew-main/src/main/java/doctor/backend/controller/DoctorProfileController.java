package doctor.backend.controller;

import doctor.backend.entity.DoctorProfile;
import doctor.backend.security.CurrentUserProvider;
import doctor.backend.service.DoctorProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctor-profile")
@CrossOrigin(origins = "http://localhost:5173")
public class DoctorProfileController {

    private final DoctorProfileService service;
    private final CurrentUserProvider currentUserProvider;

    public DoctorProfileController(
            DoctorProfileService service,
            CurrentUserProvider currentUserProvider
    ) {
        this.service = service;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping
    public ResponseEntity<DoctorProfile> getProfile() {

        return ResponseEntity.ok(
                service.getProfile(currentUserProvider.getCurrentDoctorId())
        );
    }

    @PutMapping
    public ResponseEntity<DoctorProfile> updateProfile(
            @RequestBody DoctorProfile profile
    ) {

        return ResponseEntity.ok(
                service.saveProfile(currentUserProvider.getCurrentDoctorId(), profile)
        );
    }
}
