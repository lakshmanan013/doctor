package doctor.backend.service;

import doctor.backend.dto.medicine.MedicineRequest;
import doctor.backend.dto.medicine.MedicineResponse;
import doctor.backend.entity.Medicine;
import doctor.backend.repository.MedicineRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    // =====================================================
    // CREATE MEDICINE
    // =====================================================

    public MedicineResponse createMedicine(MedicineRequest request) {

        if (request.getName() == null ||
                request.getName().trim().isEmpty()) {

            throw new RuntimeException("Medicine name is required");
        }

        Medicine medicine = new Medicine();

        medicine.setName(request.getName());
        medicine.setCategory(request.getCategory());
        medicine.setManufacturer(request.getManufacturer());
        medicine.setDescription(request.getDescription());
        medicine.setDosageForm(request.getDosageForm());
        medicine.setStrength(request.getStrength());
        medicine.setUnit(request.getUnit());
        medicine.setPrice(request.getPrice());
        medicine.setStockQuantity(request.getStockQuantity());
        medicine.setReorderLevel(request.getReorderLevel());
        medicine.setStatus(request.getStatus());

        if (medicine.getStatus() == null ||
                medicine.getStatus().trim().isEmpty()) {

            medicine.setStatus("Active");
        }

        if (medicine.getStockQuantity() == null) {
            medicine.setStockQuantity(0);
        }

        if (medicine.getReorderLevel() == null) {
            medicine.setReorderLevel(0);
        }

        Medicine savedMedicine =
                medicineRepository.save(medicine);

        return mapToResponse(savedMedicine);
    }

    // =====================================================
    // GET ALL MEDICINES
    // =====================================================

    @Transactional(readOnly = true)
    public List<MedicineResponse> getAllMedicines() {

        return medicineRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET MEDICINE BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public MedicineResponse getMedicineById(Long id) {

        Medicine medicine = medicineRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Medicine not found with id: " + id
                        )
                );

        return mapToResponse(medicine);
    }

    // =====================================================
    // SEARCH MEDICINES
    // =====================================================

    @Transactional(readOnly = true)
    public List<MedicineResponse> searchMedicines(String name) {

        return medicineRepository
                .findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY CATEGORY
    // =====================================================

    @Transactional(readOnly = true)
    public List<MedicineResponse> getByCategory(
            String category) {

        return medicineRepository
                .findByCategoryIgnoreCase(category)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY STATUS
    // =====================================================

    @Transactional(readOnly = true)
    public List<MedicineResponse> getByStatus(
            String status) {

        return medicineRepository
                .findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // UPDATE MEDICINE
    // =====================================================

    public MedicineResponse updateMedicine(
            Long id,
            MedicineRequest request) {

        Medicine medicine = medicineRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Medicine not found with id: " + id
                        )
                );

        medicine.setName(request.getName());
        medicine.setCategory(request.getCategory());
        medicine.setManufacturer(request.getManufacturer());
        medicine.setDescription(request.getDescription());
        medicine.setDosageForm(request.getDosageForm());
        medicine.setStrength(request.getStrength());
        medicine.setUnit(request.getUnit());
        medicine.setPrice(request.getPrice());
        medicine.setStockQuantity(request.getStockQuantity());
        medicine.setReorderLevel(request.getReorderLevel());
        medicine.setStatus(request.getStatus());

        Medicine updatedMedicine =
                medicineRepository.save(medicine);

        return mapToResponse(updatedMedicine);
    }

    // =====================================================
    // UPDATE STOCK
    // =====================================================

    public MedicineResponse updateStock(
            Long id,
            Integer quantity) {

        if (quantity == null) {
            throw new RuntimeException(
                    "Stock quantity is required"
            );
        }

        Medicine medicine = medicineRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Medicine not found with id: " + id
                        )
                );

        medicine.setStockQuantity(quantity);

        Medicine updatedMedicine =
                medicineRepository.save(medicine);

        return mapToResponse(updatedMedicine);
    }

    // =====================================================
    // DELETE MEDICINE
    // =====================================================

    public void deleteMedicine(Long id) {

        if (!medicineRepository.existsById(id)) {

            throw new RuntimeException(
                    "Medicine not found with id: " + id
            );
        }

        medicineRepository.deleteById(id);
    }

    // =====================================================
    // ENTITY → RESPONSE
    // =====================================================

    private MedicineResponse mapToResponse(
            Medicine medicine) {

        MedicineResponse response =
                new MedicineResponse();

        response.setId(medicine.getId());
        response.setName(medicine.getName());
        response.setCategory(medicine.getCategory());
        response.setManufacturer(
                medicine.getManufacturer()
        );
        response.setDescription(
                medicine.getDescription()
        );
        response.setDosageForm(
                medicine.getDosageForm()
        );
        response.setStrength(
                medicine.getStrength()
        );
        response.setUnit(
                medicine.getUnit()
        );
        response.setPrice(
                medicine.getPrice()
        );
        response.setStockQuantity(
                medicine.getStockQuantity()
        );
        response.setReorderLevel(
                medicine.getReorderLevel()
        );
        response.setStatus(
                medicine.getStatus()
        );
        response.setCreatedAt(
                medicine.getCreatedAt()
        );
        response.setUpdatedAt(
                medicine.getUpdatedAt()
        );

        return response;
    }
}