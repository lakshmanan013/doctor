package doctor.backend.repository;

import doctor.backend.entity.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {

    // Get emails by recipient
    List<EmailLog> findByRecipient(String recipient);

    // Get emails by status
    List<EmailLog> findByStatus(String status);

    // Get emails by type
    List<EmailLog> findByType(String type);

    // Get emails by provider
    List<EmailLog> findByProvider(String provider);

    // Get emails by recipient and status
    List<EmailLog> findByRecipientAndStatus(
            String recipient,
            String status
    );

    // Get email by provider message ID
    List<EmailLog> findByProviderMessageId(
            String providerMessageId
    );
}