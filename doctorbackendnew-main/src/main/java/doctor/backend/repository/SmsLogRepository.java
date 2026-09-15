package doctor.backend.repository;

import doctor.backend.entity.SmsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SmsLogRepository extends JpaRepository<SmsLog, Long> {

    // Get SMS logs by phone number
    List<SmsLog> findByPhoneNumber(String phoneNumber);

    // Get SMS logs by status
    List<SmsLog> findByStatus(String status);

    // Get SMS logs by type
    List<SmsLog> findByType(String type);

    // Get SMS logs by provider
    List<SmsLog> findByProvider(String provider);

    // Get SMS logs by phone number and status
    List<SmsLog> findByPhoneNumberAndStatus(
            String phoneNumber,
            String status
    );

    // Get SMS logs by provider message ID
    List<SmsLog> findByProviderMessageId(
            String providerMessageId
    );
}