package com.example.captcha.controller;

import com.example.captcha.model.CaptchaChallenge;
import com.example.captcha.model.CaptchaVerifyRequest;
import com.example.captcha.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/captcha")
@RequiredArgsConstructor
public class CaptchaController {
    private final CaptchaService captchaService;

    @GetMapping("/generate")
    public ResponseEntity<StreamingResponseBody> generateCaptcha() throws IOException {
        CaptchaChallenge captcha = captchaService.generateCaptcha();
        BufferedImage image = captcha.content();
        MediaType contentType = captcha.contentType();
        StreamingResponseBody responseBody = (outputStream) -> {
            ImageIO.write(image, contentType.getSubtype(), outputStream);
        };
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(contentType)
                .header("X-Captcha-Token", captcha.token())
                .header("X-Captcha-TTL", String.valueOf(captcha.ttl().getSeconds()))
                .body(responseBody);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCaptcha(@Validated @RequestBody CaptchaVerifyRequest verifyRequest) {
        boolean valid = captchaService.verifyCaptcha(verifyRequest.token(), verifyRequest.captchaText());
        return valid ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }
}
