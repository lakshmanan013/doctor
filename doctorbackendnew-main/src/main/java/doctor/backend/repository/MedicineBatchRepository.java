package doctor.backend.repository;

import doctor.backend.entity.MedicineBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineBatchRepository
        extends JpaRepository<MedicineBatch, Long> {

    // Get all batches for a medicine
    List<MedicineBatch> findByMedicineId(Long medicineId);

    // Find a specific batch number
    Optional<MedicineBatch> findByBatchNumber(String batchNumber);

    // Get batches expiring before a date
    List<MedicineBatch> findByExpiryDateBefore(LocalDate date);

    // Get batches expiring between dates
    List<MedicineBatch> findByExpiryDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    // Get batches by status
    List<MedicineBatch> findByStatus(String status);

    // Get batches for a medicine by status
    List<MedicineBatch> findByMedicineIdAndStatus(
            Long medicineId,
            String status
    );

    // Get batches with available stock
    List<MedicineBatch>
    findByRemainingQuantityGreaterThan(Integer quantity);
}