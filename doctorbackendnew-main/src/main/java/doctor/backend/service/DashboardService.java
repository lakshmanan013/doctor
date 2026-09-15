package doctor.backend.service;

import doctor.backend.dto.dashboard.DashboardStatsResponse;
import doctor.backend.entity.Invoice;
import doctor.backend.repository.*;
import doctor.backend.security.CurrentUserProvider;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final VaccinationRepository vaccinationRepository;
    private final InvoiceRepository invoiceRepository;
    private final NotificationRepository notificationRepository;
    private final CurrentUserProvider currentUserProvider;
    private final AppointmentService appointmentService;
    private final PatientService patientService;

    public DashboardService(
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            PrescriptionRepository prescriptionRepository,
            MedicalRecordRepository medicalRecordRepository,
            VaccinationRepository vaccinationRepository,
            InvoiceRepository invoiceRepository,
            NotificationRepository notificationRepository,
            CurrentUserProvider currentUserProvider,
            AppointmentService appointmentService,
            PatientService patientService) {

        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.invoiceRepository = invoiceRepository;
        this.notificationRepository = notificationRepository;
        this.currentUserProvider = currentUserProvider;
        this.appointmentService = appointmentService;
        this.patientService = patientService;
    }

    public DashboardStatsResponse getDashboardStats() {
        Long doctorId = currentUserProvider.getCurrentDoctorId();

        DashboardStatsResponse stats = new DashboardStatsResponse();

        stats.setTotalPatients(patientRepository.countByDoctorId(doctorId));
        stats.setTodayAppointments(appointmentRepository.countByAppointmentDateAndDoctorId(LocalDate.now(), doctorId));
        stats.setTotalAppointments(appointmentRepository.countByDoctorId(doctorId));
        stats.setTotalPrescriptions(prescriptionRepository.countByDoctorId(doctorId));
        stats.setTotalMedicalRecords(medicalRecordRepository.countByDoctorId(doctorId));
        stats.setTotalVaccinations(vaccinationRepository.countByDoctorId(doctorId));
        stats.setTotalInvoices(invoiceRepository.countByDoctorId(doctorId));
        stats.setUnreadNotifications(notificationRepository.countByUserIdAndReadFalse(doctorId));

        List<Invoice> invoices = invoiceRepository.findByDoctorId(doctorId);
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal pendingRevenue = BigDecimal.ZERO;
        for (Invoice inv : invoices) {
            if (inv.getPaidAmount() != null) {
                totalRevenue = totalRevenue.add(inv.getPaidAmount());
            }
            if (inv.getDueAmount() != null) {
                pendingRevenue = pendingRevenue.add(inv.getDueAmount());
            }
        }
        stats.setTotalRevenue(totalRevenue);
        stats.setPendingRevenue(pendingRevenue);

        // Doctor-scoped today appointments & recent patients
        stats.setTodayAppointmentsList(appointmentService.getAppointmentsByDate(LocalDate.now()));

        var patients = patientService.getAllPatients();
        stats.setRecentPatients(patients.stream().limit(10).toList());

        return stats;
    }
}
