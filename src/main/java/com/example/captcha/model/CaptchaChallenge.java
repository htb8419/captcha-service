package com.example.captcha.model;

import org.springframework.http.MediaType;

import java.awt.image.BufferedImage;
import java.time.Duration;

public record CaptchaChallenge(String token,
                               MediaType contentType,
                               BufferedImage content,
                               Duration ttl) {
}
