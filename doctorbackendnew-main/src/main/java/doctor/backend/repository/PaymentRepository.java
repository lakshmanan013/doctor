package doctor.backend.repository;

import doctor.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Get all payments for an invoice
    List<Payment> findByInvoiceId(Long invoiceId);

    // Get all payments for an owner
    List<Payment> findByOwnerId(Long ownerId);

    // Get all payments for a patient
    List<Payment> findByPatientId(Long patientId);

    // Get payments by status
    List<Payment> findByStatus(String status);

    // Get payments by payment method
    List<Payment> findByPaymentMethod(String paymentMethod);

    // Get invoice payments by status
    List<Payment> findByInvoiceIdAndStatus(
            Long invoiceId,
            String status
    );

    // Find payment by transaction ID
    List<Payment> findByTransactionId(
            String transactionId
    );

    // Find payment by reference number
    List<Payment> findByReferenceNumber(
            String referenceNumber
    );

    // Check whether transaction already exists
    boolean existsByTransactionId(
            String transactionId
    );

    // Check whether reference number already exists
    boolean existsByReferenceNumber(
            String referenceNumber
    );

    // Doctor-specific methods
    List<Payment> findByDoctorId(Long doctorId);

    java.util.Optional<Payment> findByIdAndDoctorId(Long id, Long doctorId);

    List<Payment> findByInvoiceIdAndDoctorId(Long invoiceId, Long doctorId);

    List<Payment> findByOwnerIdAndDoctorId(Long ownerId, Long doctorId);

    List<Payment> findByPatientIdAndDoctorId(Long patientId, Long doctorId);

    List<Payment> findByStatusAndDoctorId(String status, Long doctorId);

    List<Payment> findByPaymentMethodAndDoctorId(String paymentMethod, Long doctorId);

    long countByDoctorId(Long doctorId);

    List<Payment> findByDoctorIdIsNull();
}