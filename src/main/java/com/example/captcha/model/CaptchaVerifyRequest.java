package com.example.captcha.model;

public record CaptchaVerifyRequest(String token,
                                   String captchaText) {
}
