package doctor.backend.util;

import java.security.SecureRandom;

/**
 * Generates the 6-digit numeric codes used by the OTP-based
 * forgot-password flow (see AuthService).
 */
public class OtpUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private OtpUtil() {
    }

    /** Returns a zero-padded 6-digit code, e.g. "042817". */
    public static String generateOtp() {
        int code = RANDOM.nextInt(1_000_000);
        return String.format("%06d", code);
    }
}
