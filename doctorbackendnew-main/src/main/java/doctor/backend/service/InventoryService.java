package doctor.backend.service;

import doctor.backend.dto.inventory.MedicineBatchRequest;
import doctor.backend.dto.inventory.MedicineBatchResponse;
import doctor.backend.dto.inventory.StockMovementRequest;
import doctor.backend.dto.inventory.StockMovementResponse;
import doctor.backend.dto.notification.NotificationRequest;
import doctor.backend.entity.Medicine;
import doctor.backend.entity.MedicineBatch;
import doctor.backend.entity.StockMovement;
import doctor.backend.repository.MedicineBatchRepository;
import doctor.backend.repository.MedicineRepository;
import doctor.backend.repository.StockMovementRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class InventoryService {

    private final MedicineRepository medicineRepository;
    private final MedicineBatchRepository medicineBatchRepository;
    private final StockMovementRepository stockMovementRepository;
    private final NotificationService notificationService;

    public InventoryService(
            MedicineRepository medicineRepository,
            MedicineBatchRepository medicineBatchRepository,
            StockMovementRepository stockMovementRepository,
            NotificationService notificationService) {

        this.medicineRepository = medicineRepository;
        this.medicineBatchRepository = medicineBatchRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.notificationService = notificationService;
    }

    // =====================================================
    // CREATE MEDICINE BATCH
    // =====================================================

    public MedicineBatchResponse createBatch(
            MedicineBatchRequest request) {

        if (request.getMedicineId() == null) {
            throw new RuntimeException(
                    "Medicine ID is required"
            );
        }

        Medicine medicine = medicineRepository
                .findById(request.getMedicineId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Medicine not found with id: "
                                        + request.getMedicineId()
                        )
                );

        if (request.getBatchNumber() == null ||
                request.getBatchNumber().trim().isEmpty()) {

            throw new RuntimeException(
                    "Batch number is required"
            );
        }

        if (request.getQuantity() == null ||
                request.getQuantity() < 0) {

            throw new RuntimeException(
                    "Valid quantity is required"
            );
        }

        MedicineBatch batch = new MedicineBatch();

        batch.setMedicine(medicine);
        batch.setBatchNumber(request.getBatchNumber());
        batch.setManufacturingDate(
                request.getManufacturingDate()
        );
        batch.setExpiryDate(
                request.getExpiryDate()
        );
        batch.setQuantity(request.getQuantity());

        if (request.getRemainingQuantity() != null) {
            batch.setRemainingQuantity(
                    request.getRemainingQuantity()
            );
        } else {
            batch.setRemainingQuantity(
                    request.getQuantity()
            );
        }

        batch.setPurchasePrice(
                request.getPurchasePrice()
        );

        batch.setSellingPrice(
                request.getSellingPrice()
        );

        batch.setSupplierName(
                request.getSupplierName()
        );

        batch.setSupplierContact(
                request.getSupplierContact()
        );

        if (request.getStatus() != null &&
                !request.getStatus().trim().isEmpty()) {

            batch.setStatus(request.getStatus());

        } else {

            batch.setStatus("Active");
        }

        MedicineBatch savedBatch =
                medicineBatchRepository.save(batch);

        return mapBatchToResponse(savedBatch);
    }

    // =====================================================
    // GET ALL BATCHES
    // =====================================================

    @Transactional(readOnly = true)
    public List<MedicineBatchResponse> getAllBatches() {

        return medicineBatchRepository.findAll()
                .stream()
                .map(this::mapBatchToResponse)
                .toList();
    }

    // =====================================================
    // GET BATCH BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public MedicineBatchResponse getBatchById(Long id) {

        MedicineBatch batch =
                medicineBatchRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Medicine batch not found with id: "
                                                + id
                                )
                        );

        return mapBatchToResponse(batch);
    }

    // =====================================================
    // GET BATCHES BY MEDICINE
    // =====================================================

    @Transactional(readOnly = true)
    public List<MedicineBatchResponse> getBatchesByMedicine(
            Long medicineId) {

        return medicineBatchRepository
                .findByMedicineId(medicineId)
                .stream()
                .map(this::mapBatchToResponse)
                .toList();
    }

    // =====================================================
    // GET BATCH BY BATCH NUMBER
    // =====================================================

    @Transactional(readOnly = true)
    public MedicineBatchResponse getBatchByNumber(
            String batchNumber) {

        MedicineBatch batch =
                medicineBatchRepository
                        .findByBatchNumber(batchNumber)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Batch not found: "
                                                + batchNumber
                                )
                        );

        return mapBatchToResponse(batch);
    }

    // =====================================================
    // GET EXPIRING BATCHES
    // =====================================================

    @Transactional(readOnly = true)
    public List<MedicineBatchResponse> getExpiringBatches(
            LocalDate date) {

        return medicineBatchRepository
                .findByExpiryDateBefore(date)
                .stream()
                .map(this::mapBatchToResponse)
                .toList();
    }

    // =====================================================
    // GET BATCHES BETWEEN EXPIRY DATES
    // =====================================================

    @Transactional(readOnly = true)
    public List<MedicineBatchResponse>
    getBatchesExpiringBetween(
            LocalDate startDate,
            LocalDate endDate) {

        return medicineBatchRepository
                .findByExpiryDateBetween(
                        startDate,
                        endDate
                )
                .stream()
                .map(this::mapBatchToResponse)
                .toList();
    }

    // =====================================================
    // UPDATE BATCH
    // =====================================================

    public MedicineBatchResponse updateBatch(
            Long id,
            MedicineBatchRequest request) {

        MedicineBatch batch =
                medicineBatchRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Medicine batch not found with id: "
                                                + id
                                )
                        );

        if (request.getMedicineId() != null) {

            Medicine medicine =
                    medicineRepository.findById(
                            request.getMedicineId()
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "Medicine not found with id: "
                                            + request.getMedicineId()
                            )
                    );

            batch.setMedicine(medicine);
        }

        batch.setBatchNumber(
                request.getBatchNumber()
        );

        batch.setManufacturingDate(
                request.getManufacturingDate()
        );

        batch.setExpiryDate(
                request.getExpiryDate()
        );

        batch.setQuantity(
                request.getQuantity()
        );

        if (request.getRemainingQuantity() != null) {

            batch.setRemainingQuantity(
                    request.getRemainingQuantity()
            );
        }

        batch.setPurchasePrice(
                request.getPurchasePrice()
        );

        batch.setSellingPrice(
                request.getSellingPrice()
        );

        batch.setSupplierName(
                request.getSupplierName()
        );

        batch.setSupplierContact(
                request.getSupplierContact()
        );

        batch.setStatus(
                request.getStatus()
        );

        MedicineBatch updatedBatch =
                medicineBatchRepository.save(batch);

        return mapBatchToResponse(updatedBatch);
    }

    // =====================================================
    // DELETE BATCH
    // =====================================================

    public void deleteBatch(Long id) {

        if (!medicineBatchRepository.existsById(id)) {

            throw new RuntimeException(
                    "Medicine batch not found with id: "
                            + id
            );
        }

        medicineBatchRepository.deleteById(id);
    }

    // =====================================================
    // CREATE STOCK MOVEMENT
    // =====================================================

    public StockMovementResponse createStockMovement(
            StockMovementRequest request) {

        if (request.getMedicineId() == null) {

            throw new RuntimeException(
                    "Medicine ID is required"
            );
        }

        if (request.getMovementType() == null ||
                request.getMovementType().trim().isEmpty()) {

            throw new RuntimeException(
                    "Movement type is required"
            );
        }

        if (request.getQuantity() == null ||
                request.getQuantity() <= 0) {

            throw new RuntimeException(
                    "Quantity must be greater than zero"
            );
        }

        Medicine medicine =
                medicineRepository.findById(
                        request.getMedicineId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Medicine not found with id: "
                                        + request.getMedicineId()
                        )
                );

        MedicineBatch batch = null;

        if (request.getBatchId() != null) {

            batch =
                    medicineBatchRepository.findById(
                            request.getBatchId()
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "Medicine batch not found with id: "
                                            + request.getBatchId()
                            )
                    );
        }

        int previousStock =
                medicine.getStockQuantity() == null
                        ? 0
                        : medicine.getStockQuantity();

        int quantity = request.getQuantity();

        String movementType =
                request.getMovementType()
                        .trim()
                        .toUpperCase();

        int newStock;

        // =================================================
        // STOCK IN
        // =================================================

        if (isStockIn(movementType)) {

            newStock = previousStock + quantity;

        }
        // =================================================
        // STOCK OUT
        // =================================================
        else if (isStockOut(movementType)) {

            if (previousStock < quantity) {

                throw new RuntimeException(
                        "Insufficient medicine stock. "
                                + "Available: "
                                + previousStock
                                + ", Requested: "
                                + quantity
                );
            }

            newStock = previousStock - quantity;

        }
        // =================================================
        // ADJUSTMENT
        // =================================================
        else {

            newStock = quantity;
        }

        medicine.setStockQuantity(newStock);

        medicineRepository.save(medicine);

        // Flag it for the doctor once stock drops to/under the reorder
        // level, so restocking doesn't rely on someone noticing the
        // inventory page. Doesn't block the movement if it fails.
        if (medicine.getReorderLevel() != null
                && newStock <= medicine.getReorderLevel()) {

            try {
                NotificationRequest notification = new NotificationRequest();
                notification.setTitle("Low stock");
                notification.setMessage(
                        (medicine.getName() != null ? medicine.getName() : "A medicine")
                                + " is down to " + newStock
                                + " unit" + (newStock == 1 ? "" : "s")
                                + " (reorder level: " + medicine.getReorderLevel() + ")"
                );
                notification.setType("stock");
                notificationService.createNotification(notification);
            } catch (Exception ex) {
                // Non-critical — the stock movement itself is what matters.
            }
        }

        // =================================================
        // UPDATE BATCH STOCK
        // =================================================

        if (batch != null) {

            int previousBatchStock =
                    batch.getRemainingQuantity() == null
                            ? 0
                            : batch.getRemainingQuantity();

            int newBatchStock;

            if (isStockIn(movementType)) {

                newBatchStock =
                        previousBatchStock + quantity;

            } else if (isStockOut(movementType)) {

                if (previousBatchStock < quantity) {

                    throw new RuntimeException(
                            "Insufficient batch stock. "
                                    + "Available: "
                                    + previousBatchStock
                                    + ", Requested: "
                                    + quantity
                    );
                }

                newBatchStock =
                        previousBatchStock - quantity;

            } else {

                newBatchStock = quantity;
            }

            batch.setRemainingQuantity(newBatchStock);

            medicineBatchRepository.save(batch);
        }

        // =================================================
        // SAVE MOVEMENT
        // =================================================

        StockMovement movement =
                new StockMovement();

        movement.setMedicine(medicine);
        movement.setBatch(batch);
        movement.setMovementType(movementType);
        movement.setQuantity(quantity);
        movement.setPreviousStock(previousStock);
        movement.setNewStock(newStock);
        movement.setReferenceType(
                request.getReferenceType()
        );
        movement.setReferenceId(
                request.getReferenceId()
        );
        movement.setReason(
                request.getReason()
        );
        movement.setPerformedBy(
                request.getPerformedBy()
        );

        StockMovement savedMovement =
                stockMovementRepository.save(movement);

        return mapMovementToResponse(savedMovement);
    }

    // =====================================================
    // GET ALL STOCK MOVEMENTS
    // =====================================================

    @Transactional(readOnly = true)
    public List<StockMovementResponse>
    getAllStockMovements() {

        return stockMovementRepository
                .findAll()
                .stream()
                .map(this::mapMovementToResponse)
                .toList();
    }

    // =====================================================
    // GET MOVEMENTS BY MEDICINE
    // =====================================================

    @Transactional(readOnly = true)
    public List<StockMovementResponse>
    getMovementsByMedicine(Long medicineId) {

        return stockMovementRepository
                .findByMedicineId(medicineId)
                .stream()
                .map(this::mapMovementToResponse)
                .toList();
    }

    // =====================================================
    // GET MOVEMENTS BY BATCH
    // =====================================================

    @Transactional(readOnly = true)
    public List<StockMovementResponse>
    getMovementsByBatch(Long batchId) {

        return stockMovementRepository
                .findByBatchId(batchId)
                .stream()
                .map(this::mapMovementToResponse)
                .toList();
    }

    // =====================================================
    // GET MOVEMENTS BY TYPE
    // =====================================================

    @Transactional(readOnly = true)
    public List<StockMovementResponse>
    getMovementsByType(String movementType) {

        return stockMovementRepository
                .findByMovementType(
                        movementType.toUpperCase()
                )
                .stream()
                .map(this::mapMovementToResponse)
                .toList();
    }

    // =====================================================
    // GET RECENT MOVEMENTS
    // =====================================================

    @Transactional(readOnly = true)
    public List<StockMovementResponse>
    getRecentMovements() {

        return stockMovementRepository
                .findTop20ByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapMovementToResponse)
                .toList();
    }

    // =====================================================
    // GET MOVEMENTS BETWEEN DATES
    // =====================================================

    @Transactional(readOnly = true)
    public List<StockMovementResponse>
    getMovementsBetweenDates(
            LocalDateTime startDate,
            LocalDateTime endDate) {

        return stockMovementRepository
                .findByCreatedAtBetween(
                        startDate,
                        endDate
                )
                .stream()
                .map(this::mapMovementToResponse)
                .toList();
    }

    // =====================================================
    // DELETE STOCK MOVEMENT
    // =====================================================

    public void deleteStockMovement(Long id) {

        if (!stockMovementRepository.existsById(id)) {

            throw new RuntimeException(
                    "Stock movement not found with id: "
                            + id
            );
        }

        stockMovementRepository.deleteById(id);
    }

    // =====================================================
    // STOCK IN TYPES
    // =====================================================

    private boolean isStockIn(String movementType) {

        return movementType.equals("PURCHASE")
                || movementType.equals("RETURN");
    }

    // =====================================================
    // STOCK OUT TYPES
    // =====================================================

    private boolean isStockOut(String movementType) {

        return movementType.equals("SALE")
                || movementType.equals("PRESCRIPTION")
                || movementType.equals("EXPIRED")
                || movementType.equals("DAMAGED");
    }

    // =====================================================
    // BATCH ENTITY → RESPONSE
    // =====================================================

    private MedicineBatchResponse mapBatchToResponse(
            MedicineBatch batch) {

        MedicineBatchResponse response =
                new MedicineBatchResponse();

        response.setId(batch.getId());

        if (batch.getMedicine() != null) {

            response.setMedicineId(
                    batch.getMedicine().getId()
            );

            response.setMedicineName(
                    batch.getMedicine().getName()
            );
        }

        response.setBatchNumber(
                batch.getBatchNumber()
        );

        response.setManufacturingDate(
                batch.getManufacturingDate()
        );

        response.setExpiryDate(
                batch.getExpiryDate()
        );

        response.setQuantity(
                batch.getQuantity()
        );

        response.setRemainingQuantity(
                batch.getRemainingQuantity()
        );

        response.setPurchasePrice(
                batch.getPurchasePrice()
        );

        response.setSellingPrice(
                batch.getSellingPrice()
        );

        response.setSupplierName(
                batch.getSupplierName()
        );

        response.setSupplierContact(
                batch.getSupplierContact()
        );

        response.setStatus(
                batch.getStatus()
        );

        response.setCreatedAt(
                batch.getCreatedAt()
        );

        response.setUpdatedAt(
                batch.getUpdatedAt()
        );

        return response;
    }

    // =====================================================
    // MOVEMENT ENTITY → RESPONSE
    // =====================================================

    private StockMovementResponse mapMovementToResponse(
            StockMovement movement) {

        StockMovementResponse response =
                new StockMovementResponse();

        response.setId(movement.getId());

        if (movement.getMedicine() != null) {

            response.setMedicineId(
                    movement.getMedicine().getId()
            );

            response.setMedicineName(
                    movement.getMedicine().getName()
            );
        }

        if (movement.getBatch() != null) {

            response.setBatchId(
                    movement.getBatch().getId()
            );

            response.setBatchNumber(
                    movement.getBatch().getBatchNumber()
            );
        }

        response.setMovementType(
                movement.getMovementType()
        );

        response.setQuantity(
                movement.getQuantity()
        );

        response.setPreviousStock(
                movement.getPreviousStock()
        );

        response.setNewStock(
                movement.getNewStock()
        );

        response.setReferenceType(
                movement.getReferenceType()
        );

        response.setReferenceId(
                movement.getReferenceId()
        );

        response.setReason(
                movement.getReason()
        );

        response.setPerformedBy(
                movement.getPerformedBy()
        );

        response.setCreatedAt(
                movement.getCreatedAt()
        );

        return response;
    }
}