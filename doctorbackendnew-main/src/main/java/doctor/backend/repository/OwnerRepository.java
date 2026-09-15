package doctor.backend.repository;

import doctor.backend.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    Optional<Owner> findByEmail(String email);

    Optional<Owner> findByPhone(String phone);

    // Doctor-specific methods
    List<Owner> findByDoctorId(Long doctorId);

    Optional<Owner> findByIdAndDoctorId(Long id, Long doctorId);

    boolean existsByIdAndDoctorId(Long id, Long doctorId);

    long countByDoctorId(Long doctorId);

    List<Owner> findByDoctorIdIsNull();
}