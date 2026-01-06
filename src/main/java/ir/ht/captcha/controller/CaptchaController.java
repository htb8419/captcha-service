package ir.ht.captcha.controller;

import ir.ht.captcha.model.CaptchaRequest;
import ir.ht.captcha.model.CaptchaVerifyRequest;
import ir.ht.captcha.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

@RestController
@RequestMapping("/api/v1/captcha")
@RequiredArgsConstructor
public class CaptchaController {
    private final CaptchaService captchaService;

    @PostMapping("/generate")
    public ResponseEntity<StreamingResponseBody> generateCaptcha(
            @Validated @RequestBody CaptchaRequest captchaRequest) {
        var captcha = captchaService.generateCaptcha(captchaRequest.requestKey());
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
    public ResponseEntity<?> verifyCaptcha(
            @Validated @RequestBody CaptchaVerifyRequest verifyRequest) {
        boolean valid = captchaService.verifyCaptcha(verifyRequest);
        return valid ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }
}
