package doctor.backend.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;
import doctor.backend.dto.appointment.AppointmentResponse;
import doctor.backend.dto.patient.PatientResponse;

public class DashboardStatsResponse {

    private long totalPatients;
    private long todayAppointments;
    private long totalAppointments;
    private long totalPrescriptions;
    private long totalMedicalRecords;
    private long totalVaccinations;
    private long totalInvoices;
    private BigDecimal totalRevenue;
    private BigDecimal pendingRevenue;
    private long unreadNotifications;
    private List<AppointmentResponse> todayAppointmentsList;
    private List<PatientResponse> recentPatients;

    public long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(long totalPatients) {
        this.totalPatients = totalPatients;
    }

    public long getTodayAppointments() {
        return todayAppointments;
    }

    public void setTodayAppointments(long todayAppointments) {
        this.todayAppointments = todayAppointments;
    }

    public long getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(long totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public long getTotalPrescriptions() {
        return totalPrescriptions;
    }

    public void setTotalPrescriptions(long totalPrescriptions) {
        this.totalPrescriptions = totalPrescriptions;
    }

    public long getTotalMedicalRecords() {
        return totalMedicalRecords;
    }

    public void setTotalMedicalRecords(long totalMedicalRecords) {
        this.totalMedicalRecords = totalMedicalRecords;
    }

    public long getTotalVaccinations() {
        return totalVaccinations;
    }

    public void setTotalVaccinations(long totalVaccinations) {
        this.totalVaccinations = totalVaccinations;
    }

    public long getTotalInvoices() {
        return totalInvoices;
    }

    public void setTotalInvoices(long totalInvoices) {
        this.totalInvoices = totalInvoices;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getPendingRevenue() {
        return pendingRevenue;
    }

    public void setPendingRevenue(BigDecimal pendingRevenue) {
        this.pendingRevenue = pendingRevenue;
    }

    public long getUnreadNotifications() {
        return unreadNotifications;
    }

    public void setUnreadNotifications(long unreadNotifications) {
        this.unreadNotifications = unreadNotifications;
    }

    public List<AppointmentResponse> getTodayAppointmentsList() {
        return todayAppointmentsList;
    }

    public void setTodayAppointmentsList(List<AppointmentResponse> todayAppointmentsList) {
        this.todayAppointmentsList = todayAppointmentsList;
    }

    public List<PatientResponse> getRecentPatients() {
        return recentPatients;
    }

    public void setRecentPatients(List<PatientResponse> recentPatients) {
        this.recentPatients = recentPatients;
    }
}
