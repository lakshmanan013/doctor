package doctor.backend.repository;

import doctor.backend.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    Optional<Medicine> findByNameIgnoreCase(String name);

    List<Medicine> findByNameContainingIgnoreCase(String name);

    List<Medicine> findByCategoryIgnoreCase(String category);

    List<Medicine> findByStatus(String status);
}