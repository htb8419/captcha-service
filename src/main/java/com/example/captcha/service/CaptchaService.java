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
    private static final char[] chars = "ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789".toCharArray();
    private final CaptchaImageGenerator imageGenerator;
    private final CaptchaTokenService tokenService;
    private final CaptchaProperties properties;

    public CaptchaChallenge generateCaptcha() {
        var text = generateText();
        var image = imageGenerator.generate(text);
        String token = tokenService.createToken(text);
        return new CaptchaChallenge(token,
                MediaType.IMAGE_PNG, image,
                properties.getTTL());
    }

    public boolean verifyCaptcha(String token, String answer) {
        return tokenService.validateToken(token, answer);
    }

    private String generateText() {
        return RandomStringUtils.secure().next(6, chars);
    }
}
