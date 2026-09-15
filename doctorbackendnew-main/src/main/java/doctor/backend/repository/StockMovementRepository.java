package doctor.backend.repository;

import doctor.backend.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository
        extends JpaRepository<StockMovement, Long> {

    // Get all movements for a medicine
    List<StockMovement> findByMedicineId(Long medicineId);

    // Get all movements for a batch
    List<StockMovement> findByBatchId(Long batchId);

    // Get movements by type
    List<StockMovement> findByMovementType(String movementType);

    // Get movements for a medicine by type
    List<StockMovement> findByMedicineIdAndMovementType(
            Long medicineId,
            String movementType
    );

    // Get movements between dates
    List<StockMovement> findByCreatedAtBetween(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // Get recent movements
    List<StockMovement> findTop20ByOrderByCreatedAtDesc();
}