package ir.ht.captcha.model;

public record CaptchaVerifyRequest(String token,
                                   String captchaText) {
}
