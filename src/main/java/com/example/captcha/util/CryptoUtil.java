package com.example.captcha.util;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class CryptoUtil {
    private static final String ALGORITHM = "HmacSHA256";

    private CryptoUtil() {
    }

    public static String shaHex(String secret, String data) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM));
            byte[] res = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(res);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // constant time equals to avoid timing attacks
    public static boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8));
    }
}
