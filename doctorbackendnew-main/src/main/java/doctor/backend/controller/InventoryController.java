package doctor.backend.controller;

import doctor.backend.dto.inventory.MedicineBatchRequest;
import doctor.backend.dto.inventory.MedicineBatchResponse;
import doctor.backend.dto.inventory.StockMovementRequest;
import doctor.backend.dto.inventory.StockMovementResponse;
import doctor.backend.service.InventoryService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(
            InventoryService inventoryService) {

        this.inventoryService = inventoryService;
    }

    // =====================================================
    // CREATE MEDICINE BATCH
    // POST /api/inventory/batches
    // =====================================================

    @PostMapping("/batches")
    public ResponseEntity<MedicineBatchResponse> createBatch(
            @RequestBody MedicineBatchRequest request) {

        MedicineBatchResponse response =
                inventoryService.createBatch(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    // =====================================================
    // GET ALL BATCHES
    // GET /api/inventory/batches
    // =====================================================

    @GetMapping("/batches")
    public ResponseEntity<List<MedicineBatchResponse>>
    getAllBatches() {

        return ResponseEntity.ok(
                inventoryService.getAllBatches()
        );
    }

    // =====================================================
    // GET BATCH BY ID
    // GET /api/inventory/batches/{id}
    // =====================================================

    @GetMapping("/batches/{id}")
    public ResponseEntity<MedicineBatchResponse>
    getBatchById(@PathVariable Long id) {

        return ResponseEntity.ok(
                inventoryService.getBatchById(id)
        );
    }

    // =====================================================
    // GET BATCHES BY MEDICINE
    // GET /api/inventory/batches/medicine/{medicineId}
    // =====================================================

    @GetMapping("/batches/medicine/{medicineId}")
    public ResponseEntity<List<MedicineBatchResponse>>
    getBatchesByMedicine(
            @PathVariable Long medicineId) {

        return ResponseEntity.ok(
                inventoryService
                        .getBatchesByMedicine(medicineId)
        );
    }

    // =====================================================
    // GET BATCH BY NUMBER
    // GET /api/inventory/batches/number/{batchNumber}
    // =====================================================

    @GetMapping("/batches/number/{batchNumber}")
    public ResponseEntity<MedicineBatchResponse>
    getBatchByNumber(
            @PathVariable String batchNumber) {

        return ResponseEntity.ok(
                inventoryService
                        .getBatchByNumber(batchNumber)
        );
    }

    // =====================================================
    // GET EXPIRING BATCHES
    // GET /api/inventory/batches/expiring?date=2026-12-31
    // =====================================================

    @GetMapping("/batches/expiring")
    public ResponseEntity<List<MedicineBatchResponse>>
    getExpiringBatches(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        return ResponseEntity.ok(
                inventoryService
                        .getExpiringBatches(date)
        );
    }

    // =====================================================
    // GET BATCHES BETWEEN EXPIRY DATES
    // GET /api/inventory/batches/expiry-range
    // =====================================================

    @GetMapping("/batches/expiry-range")
    public ResponseEntity<List<MedicineBatchResponse>>
    getBatchesExpiringBetween(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return ResponseEntity.ok(
                inventoryService
                        .getBatchesExpiringBetween(
                                startDate,
                                endDate
                        )
        );
    }

    // =====================================================
    // UPDATE BATCH
    // PUT /api/inventory/batches/{id}
    // =====================================================

    @PutMapping("/batches/{id}")
    public ResponseEntity<MedicineBatchResponse>
    updateBatch(
            @PathVariable Long id,
            @RequestBody MedicineBatchRequest request) {

        return ResponseEntity.ok(
                inventoryService.updateBatch(
                        id,
                        request
                )
        );
    }

    // =====================================================
    // DELETE BATCH
    // DELETE /api/inventory/batches/{id}
    // =====================================================

    @DeleteMapping("/batches/{id}")
    public ResponseEntity<Void> deleteBatch(
            @PathVariable Long id) {

        inventoryService.deleteBatch(id);

        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // CREATE STOCK MOVEMENT
    // POST /api/inventory/movements
    // =====================================================

    @PostMapping("/movements")
    public ResponseEntity<StockMovementResponse>
    createStockMovement(
            @RequestBody StockMovementRequest request) {

        StockMovementResponse response =
                inventoryService.createStockMovement(
                        request
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    // =====================================================
    // GET ALL STOCK MOVEMENTS
    // GET /api/inventory/movements
    // =====================================================

    @GetMapping("/movements")
    public ResponseEntity<List<StockMovementResponse>>
    getAllStockMovements() {

        return ResponseEntity.ok(
                inventoryService
                        .getAllStockMovements()
        );
    }

    // =====================================================
    // GET MOVEMENTS BY MEDICINE
    // GET /api/inventory/movements/medicine/{medicineId}
    // =====================================================

    @GetMapping("/movements/medicine/{medicineId}")
    public ResponseEntity<List<StockMovementResponse>>
    getMovementsByMedicine(
            @PathVariable Long medicineId) {

        return ResponseEntity.ok(
                inventoryService
                        .getMovementsByMedicine(medicineId)
        );
    }

    // =====================================================
    // GET MOVEMENTS BY BATCH
    // GET /api/inventory/movements/batch/{batchId}
    // =====================================================

    @GetMapping("/movements/batch/{batchId}")
    public ResponseEntity<List<StockMovementResponse>>
    getMovementsByBatch(
            @PathVariable Long batchId) {

        return ResponseEntity.ok(
                inventoryService
                        .getMovementsByBatch(batchId)
        );
    }

    // =====================================================
    // GET MOVEMENTS BY TYPE
    // GET /api/inventory/movements/type/{movementType}
    // =====================================================

    @GetMapping("/movements/type/{movementType}")
    public ResponseEntity<List<StockMovementResponse>>
    getMovementsByType(
            @PathVariable String movementType) {

        return ResponseEntity.ok(
                inventoryService
                        .getMovementsByType(movementType)
        );
    }

    // =====================================================
    // GET RECENT MOVEMENTS
    // GET /api/inventory/movements/recent
    // =====================================================

    @GetMapping("/movements/recent")
    public ResponseEntity<List<StockMovementResponse>>
    getRecentMovements() {

        return ResponseEntity.ok(
                inventoryService.getRecentMovements()
        );
    }

    // =====================================================
    // GET MOVEMENTS BETWEEN DATES
    // GET /api/inventory/movements/date-range
    // =====================================================

    @GetMapping("/movements/date-range")
    public ResponseEntity<List<StockMovementResponse>>
    getMovementsBetweenDates(
            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME
            )
            LocalDateTime startDate,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME
            )
            LocalDateTime endDate) {

        return ResponseEntity.ok(
                inventoryService
                        .getMovementsBetweenDates(
                                startDate,
                                endDate
                        )
        );
    }

    // =====================================================
    // DELETE STOCK MOVEMENT
    // DELETE /api/inventory/movements/{id}
    // =====================================================

    @DeleteMapping("/movements/{id}")
    public ResponseEntity<Void> deleteStockMovement(
            @PathVariable Long id) {

        inventoryService.deleteStockMovement(id);

        return ResponseEntity.noContent().build();
    }
}