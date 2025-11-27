package ir.ht.captcha.service;

import ir.ht.captcha.config.CaptchaProperties;
import ir.ht.captcha.util.CryptoUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaptchaTokenService {
    private static final String delimiter = ":";
    private final CaptchaProperties properties;

    public String createToken(String answer) {
        String nonce = UUID.randomUUID().toString();
        long expiry = Instant.now().plus(properties.getTTL()).getEpochSecond();
        String hmac = CryptoUtil.shaHex(properties.getSecretKey(), String.join(delimiter, nonce, String.valueOf(expiry), answer));

        String tokenRaw = String.join(delimiter,
                nonce, String.valueOf(expiry), hmac);
        return Base64.encodeBase64URLSafeString(tokenRaw.getBytes(StandardCharsets.UTF_8));
    }

    public boolean validateToken(String token, String answer) {
        try {
            String raw = new String(Base64.decodeBase64(token), StandardCharsets.UTF_8);
            String[] parts = raw.split(delimiter);
            if (parts.length != 3) return false;

            String nonce = parts[0];
            long expiry = Long.parseLong(parts[1]);
            String hmac = parts[2];

            if (Instant.now().getEpochSecond() > expiry)
                return false;

            String recomputed = CryptoUtil.shaHex(properties.getSecretKey(),
                    String.join(delimiter, nonce, String.valueOf(expiry), answer));
            return CryptoUtil.constantTimeEquals(hmac, recomputed);
        } catch (Exception e) {
            return false;
        }
    }
}
