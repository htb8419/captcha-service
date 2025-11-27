package com.example.captcha.service;

import com.example.captcha.config.CaptchaProperties;
import com.example.captcha.model.CaptchaChallenge;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CaptchaService {
    private static final char[] chars = "ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789".toCharArray();
    private final ImageGenerator imageGenerator;
    private final CaptchaTokenService tokenService;
    private final CaptchaProperties properties;
    private final CaptchaReplayProtectionService replayProtectionService;


    public CaptchaChallenge generateCaptcha() {
        var text = generateText();
        var image = imageGenerator.generate(text);
        String token = tokenService.createToken(text);
        return new CaptchaChallenge(token,
                MediaType.IMAGE_PNG, image,
                properties.getTTL());
    }

    public boolean verifyCaptcha(String token, String answer) {
        if (replayProtectionService.isUsed(token)) {
            return false;
        }

        boolean valid = tokenService.validateToken(token, answer);
        if (valid) {
            replayProtectionService.markUsed(token);
        }
        return valid;
    }

    private String generateText() {
        return RandomStringUtils.secure().next(5, chars);
    }
}
