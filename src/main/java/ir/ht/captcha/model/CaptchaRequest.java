package ir.ht.captcha.model;

import jakarta.validation.constraints.NotEmpty;

public record CaptchaRequest(@NotEmpty String requestKey) {
}
