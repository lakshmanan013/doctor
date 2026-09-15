package doctor.backend.config;

import doctor.backend.entity.*;
import doctor.backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataIsolationMigrationRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataIsolationMigrationRunner.class);

    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final VaccinationRepository vaccinationRepository;
    private final FollowUpRepository followUpRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public DataIsolationMigrationRunner(
            UserRepository userRepository,
            OwnerRepository ownerRepository,
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            MedicalRecordRepository medicalRecordRepository,
            PrescriptionRepository prescriptionRepository,
            VaccinationRepository vaccinationRepository,
            FollowUpRepository followUpRepository,
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository) {

        this.userRepository = userRepository;
        this.ownerRepository = ownerRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.followUpRepository = followUpRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Find default doctor (e.g. ID 1 or the first registered doctor)
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            log.info("DataIsolationMigrationRunner: No users found, skipping legacy data migration.");
            return;
        }

        User defaultDoctor = users.stream()
                .filter(u -> "DOCTOR".equalsIgnoreCase(u.getRole()))
                .findFirst()
                .orElse(users.get(0));

        Long defaultDoctorId = defaultDoctor.getId();
        log.info("DataIsolationMigrationRunner: Backfilling legacy records with NULL doctor_id to default doctor ID: {}", defaultDoctorId);

        // Owners
        List<Owner> unassignedOwners = ownerRepository.findByDoctorIdIsNull();
        if (!unassignedOwners.isEmpty()) {
            unassignedOwners.forEach(o -> o.setDoctorId(defaultDoctorId));
            ownerRepository.saveAll(unassignedOwners);
            log.info("Migrated {} unassigned owners to doctor {}", unassignedOwners.size(), defaultDoctorId);
        }

        // Patients
        List<Patient> unassignedPatients = patientRepository.findByDoctorIdIsNull();
        if (!unassignedPatients.isEmpty()) {
            unassignedPatients.forEach(p -> p.setDoctorId(defaultDoctorId));
            patientRepository.saveAll(unassignedPatients);
            log.info("Migrated {} unassigned patients to doctor {}", unassignedPatients.size(), defaultDoctorId);
        }

        // Appointments
        List<Appointment> unassignedAppointments = appointmentRepository.findByDoctorIdIsNull();
        if (!unassignedAppointments.isEmpty()) {
            unassignedAppointments.forEach(a -> a.setDoctorId(defaultDoctorId));
            appointmentRepository.saveAll(unassignedAppointments);
            log.info("Migrated {} unassigned appointments to doctor {}", unassignedAppointments.size(), defaultDoctorId);
        }

        // Medical Records
        List<MedicalRecord> unassignedRecords = medicalRecordRepository.findByDoctorIdIsNull();
        if (!unassignedRecords.isEmpty()) {
            unassignedRecords.forEach(m -> m.setDoctorId(defaultDoctorId));
            medicalRecordRepository.saveAll(unassignedRecords);
            log.info("Migrated {} unassigned medical records to doctor {}", unassignedRecords.size(), defaultDoctorId);
        }

        // Prescriptions
        List<Prescription> unassignedPrescriptions = prescriptionRepository.findByDoctorIdIsNull();
        if (!unassignedPrescriptions.isEmpty()) {
            unassignedPrescriptions.forEach(pr -> pr.setDoctorId(defaultDoctorId));
            prescriptionRepository.saveAll(unassignedPrescriptions);
            log.info("Migrated {} unassigned prescriptions to doctor {}", unassignedPrescriptions.size(), defaultDoctorId);
        }

        // Vaccinations
        List<Vaccination> unassignedVaccinations = vaccinationRepository.findByDoctorIdIsNull();
        if (!unassignedVaccinations.isEmpty()) {
            unassignedVaccinations.forEach(v -> v.setDoctorId(defaultDoctorId));
            vaccinationRepository.saveAll(unassignedVaccinations);
            log.info("Migrated {} unassigned vaccinations to doctor {}", unassignedVaccinations.size(), defaultDoctorId);
        }

        // FollowUps
        List<FollowUp> unassignedFollowUps = followUpRepository.findByDoctorIdIsNull();
        if (!unassignedFollowUps.isEmpty()) {
            unassignedFollowUps.forEach(f -> f.setDoctorId(defaultDoctorId));
            followUpRepository.saveAll(unassignedFollowUps);
            log.info("Migrated {} unassigned follow-ups to doctor {}", unassignedFollowUps.size(), defaultDoctorId);
        }

        // Invoices
        List<Invoice> unassignedInvoices = invoiceRepository.findByDoctorIdIsNull();
        if (!unassignedInvoices.isEmpty()) {
            unassignedInvoices.forEach(i -> i.setDoctorId(defaultDoctorId));
            invoiceRepository.saveAll(unassignedInvoices);
            log.info("Migrated {} unassigned invoices to doctor {}", unassignedInvoices.size(), defaultDoctorId);
        }

        // Payments
        List<Payment> unassignedPayments = paymentRepository.findByDoctorIdIsNull();
        if (!unassignedPayments.isEmpty()) {
            unassignedPayments.forEach(pay -> pay.setDoctorId(defaultDoctorId));
            paymentRepository.saveAll(unassignedPayments);
            log.info("Migrated {} unassigned payments to doctor {}", unassignedPayments.size(), defaultDoctorId);
        }

        log.info("DataIsolationMigrationRunner: Completed data isolation migration check.");
    }
}
