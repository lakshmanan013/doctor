package doctor.backend.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import doctor.backend.config.TwilioProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

/**
 * Thin wrapper around the Twilio SDK for SMS and WhatsApp sends. Both
 * SmsService and WhatsAppService go through here so account init and the
 * "not configured" fallback only live in one place.
 */
@Service
public class TwilioSender {

    private final TwilioProperties properties;

    public TwilioSender(TwilioProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void init() {
        if (!properties.isConfigured()) {
            return;
        }
        if (properties.isApiKey()) {
            // API Key SID + Secret + the real Account SID (3-arg form).
            Twilio.init(properties.getAccountSid(), properties.getAuthToken(), properties.getParentAccountSid());
        } else {
            // Classic Account SID + Auth Token.
            Twilio.init(properties.getAccountSid(), properties.getAuthToken());
        }
    }

    public boolean isConfigured() {
        return properties.isConfigured();
    }

    /** Plain SMS. Throws if Twilio rejects the send (bad number, no credit, etc). */
    public String sendSms(String toPhoneNumber, String body) {
        Message message = Message.creator(
                new PhoneNumber(toPhoneNumber),
                new PhoneNumber(properties.getSmsFrom()),
                body
        ).create();

        return message.getSid();
    }

    /** WhatsApp message, via Twilio's WhatsApp Business API sandbox/number. */
    public String sendWhatsApp(String toPhoneNumber, String body) {
        Message message = Message.creator(
                new PhoneNumber("whatsapp:" + toPhoneNumber),
                new PhoneNumber("whatsapp:" + properties.getWhatsappFrom()),
                body
        ).create();

        return message.getSid();
    }
}
