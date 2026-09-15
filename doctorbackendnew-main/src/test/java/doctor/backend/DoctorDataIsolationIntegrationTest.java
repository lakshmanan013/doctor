package doctor.backend;

import doctor.backend.dto.dashboard.DashboardStatsResponse;
import doctor.backend.dto.owner.OwnerRequest;
import doctor.backend.dto.owner.OwnerResponse;
import doctor.backend.dto.patient.PatientRequest;
import doctor.backend.dto.patient.PatientResponse;
import doctor.backend.entity.Medicine;
import doctor.backend.entity.User;
import doctor.backend.repository.MedicineRepository;
import doctor.backend.repository.UserRepository;
import doctor.backend.service.DashboardService;
import doctor.backend.service.MedicineService;
import doctor.backend.service.OwnerService;
import doctor.backend.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DoctorDataIsolationIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private MedicineService medicineService;

    @Autowired
    private MedicineRepository medicineRepository;

    private User doctorA;
    private User doctorB;

    @BeforeEach
    void setUp() {
        // Ensure Doctor A exists
        doctorA = userRepository.findByEmail("doctorA@test.com").orElseGet(() -> {
            User u = new User();
            u.setEmail("doctorA@test.com");
            u.setPassword("Password123!");
            u.setFullName("Dr. Doctor A");
            u.setRole("DOCTOR");
            u.setActive(true);
            u.setApprovalStatus("APPROVED");
            return userRepository.save(u);
        });

        // Ensure Doctor B exists (newly registered doctor)
        doctorB = userRepository.findByEmail("doctorB@test.com").orElseGet(() -> {
            User u = new User();
            u.setEmail("doctorB@test.com");
            u.setPassword("Password123!");
            u.setFullName("Dr. Doctor B");
            u.setRole("DOCTOR");
            u.setActive(true);
            u.setApprovalStatus("APPROVED");
            return userRepository.save(u);
        });

        // Ensure at least one medicine exists in inventory (shared across doctors)
        if (medicineRepository.count() == 0) {
            Medicine m = new Medicine();
            m.setName("Amoxicillin 250mg");
            m.setCategory("Antibiotic");
            m.setStockQuantity(100);
            m.setPrice(15.50);
            medicineRepository.save(m);
        }
    }

    private void authenticateAs(User doctor) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                doctor.getEmail(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_DOCTOR"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testDoctorDataIsolationAndSharedInventory() {
        // ==========================================
        // STEP 1: Doctor A creates owner and patient
        // ==========================================
        authenticateAs(doctorA);

        OwnerRequest ownerRequest = new OwnerRequest();
        ownerRequest.setFullName("John Doe (A's Client)");
        ownerRequest.setEmail("john.doe.a@example.com");
        ownerRequest.setPhone("9876543210");
        OwnerResponse ownerA = ownerService.createOwner(ownerRequest);
        assertNotNull(ownerA.getId());
        assertEquals(doctorA.getId(), ownerA.getDoctorId());

        PatientRequest patientRequest = new PatientRequest();
        patientRequest.setName("Buddy A");
        patientRequest.setSpecies("Dog");
        patientRequest.setBreed("Golden Retriever");
        patientRequest.setOwnerId(ownerA.getId());
        PatientResponse patientA = patientService.createPatient(patientRequest);
        assertNotNull(patientA.getId());
        assertEquals(doctorA.getId(), patientA.getDoctorId());

        // Doctor A can see their own patient
        List<PatientResponse> doctorAPatients = patientService.getAllPatients();
        assertTrue(doctorAPatients.stream().anyMatch(p -> p.getId().equals(patientA.getId())));

        // ==========================================
        // STEP 2: Doctor B logs in (new doctor)
        // ==========================================
        authenticateAs(doctorB);

        // Doctor B must NOT see Doctor A's patient
        List<PatientResponse> doctorBPatients = patientService.getAllPatients();
        assertFalse(doctorBPatients.stream().anyMatch(p -> p.getId().equals(patientA.getId())),
                "Doctor B must not see Doctor A's patient!");

        // Doctor B cannot access Doctor A's patient by ID
        assertThrows(RuntimeException.class, () -> patientService.getPatientById(patientA.getId()),
                "Doctor B fetching Doctor A's patient ID directly must throw not found exception!");

        // Doctor B's dashboard must be empty
        DashboardStatsResponse dashboardB = dashboardService.getDashboardStats();
        assertEquals(0, dashboardB.getTotalPatients(), "Doctor B should have 0 patients on dashboard");
        assertEquals(0, dashboardB.getTotalAppointments(), "Doctor B should have 0 appointments on dashboard");
        assertEquals(0, dashboardB.getTotalMedicalRecords(), "Doctor B should have 0 medical records on dashboard");
        assertEquals(0, dashboardB.getTotalInvoices(), "Doctor B should have 0 invoices on dashboard");

        // ==========================================
        // STEP 3: Shared Medicine Inventory
        // ==========================================
        // Doctor B MUST be able to see the shared medicine inventory
        List<?> medicinesForDoctorB = medicineService.getAllMedicines();
        assertFalse(medicinesForDoctorB.isEmpty(), "Doctor B must see the shared medicine inventory!");

        // ==========================================
        // STEP 4: Doctor B creates their own record
        // ==========================================
        OwnerRequest ownerBRequest = new OwnerRequest();
        ownerBRequest.setFullName("Alice Smith (B's Client)");
        ownerBRequest.setEmail("alice.smith.b@example.com");
        ownerBRequest.setPhone("9123456780");
        OwnerResponse ownerB = ownerService.createOwner(ownerBRequest);

        PatientRequest patientBRequest = new PatientRequest();
        patientBRequest.setName("Milo B");
        patientBRequest.setSpecies("Cat");
        patientBRequest.setBreed("Persian");
        patientBRequest.setOwnerId(ownerB.getId());
        PatientResponse patientB = patientService.createPatient(patientBRequest);

        // Doctor B sees Milo B
        List<PatientResponse> bPatientsAfterCreate = patientService.getAllPatients();
        assertEquals(1, bPatientsAfterCreate.size());
        assertEquals("Milo B", bPatientsAfterCreate.get(0).getName());

        // Doctor A does NOT see Milo B
        authenticateAs(doctorA);
        List<PatientResponse> aPatientsAfterDoctorBCreated = patientService.getAllPatients();
        assertFalse(aPatientsAfterDoctorBCreated.stream().anyMatch(p -> p.getId().equals(patientB.getId())),
                "Doctor A must not see Doctor B's patient Milo B!");
    }
}
