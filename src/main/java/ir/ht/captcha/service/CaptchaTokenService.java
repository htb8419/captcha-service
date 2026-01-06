package ir.ht.captcha.service;

import ir.ht.captcha.config.CaptchaProperties;
import ir.ht.captcha.util.CryptoUtil;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class CaptchaTokenService {
    private static final String delimiter = ":";
    private final String secretKey;
    private final Duration TTL;

    public CaptchaTokenService(CaptchaProperties properties) {
        this.secretKey = properties.getSecretKey();
        this.TTL = properties.getTTL();
    }

    public String createToken(String key) {
        String nonce = UUID.randomUUID().toString();
        long expiry = Instant.now().plus(TTL).getEpochSecond();
        String hmac = generateHax(nonce, expiry, key);
        String tokenRaw = joinWithDelimiter(nonce, String.valueOf(expiry), hmac);
        return Base64.encodeBase64URLSafeString(tokenRaw.getBytes(StandardCharsets.UTF_8));
    }

    public boolean validateToken(String token, String key) {
        try {
            String raw = new String(Base64.decodeBase64(token), StandardCharsets.UTF_8);
            String[] parts = raw.split(delimiter);
            if (parts.length != 3) return false;

            String nonce = parts[0];
            long expiry = Long.parseLong(parts[1]);
            String hmac = parts[2];

            if (Instant.now().getEpochSecond() > expiry)
                return false;

            String recomputed = generateHax(nonce, expiry, key);
            return CryptoUtil.constantTimeEquals(hmac, recomputed);
        } catch (Exception e) {
            return false;
        }
    }

    public String joinWithDelimiter(String... args) {
        return String.join(delimiter, args);
    }

    private String generateHax(String nonce, long expiry, String key) {
        return CryptoUtil.shaHex(secretKey,
                joinWithDelimiter(nonce, String.valueOf(expiry), key));
    }
}
