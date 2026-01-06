package ir.ht.captcha.model;

import jakarta.validation.constraints.NotEmpty;

public record CaptchaVerifyRequest(@NotEmpty String requestKey,
                                   @NotEmpty String token,
                                   @NotEmpty String answer) {
}
