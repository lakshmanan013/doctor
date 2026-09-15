package doctor.backend.repository;

import doctor.backend.entity.WhatsAppLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WhatsAppLogRepository
        extends JpaRepository<WhatsAppLog, Long> {

    List<WhatsAppLog> findByPhoneNumber(String phoneNumber);

    List<WhatsAppLog> findByStatus(String status);

    List<WhatsAppLog> findByType(String type);

    List<WhatsAppLog> findByProvider(String provider);

    List<WhatsAppLog> findByPhoneNumberAndStatus(
            String phoneNumber,
            String status
    );

    List<WhatsAppLog> findByProviderMessageId(
            String providerMessageId
    );
}