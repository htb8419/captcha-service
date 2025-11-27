package com.example.captcha.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "captcha")
public class CaptchaProperties {
    String secretKey;
    Duration TTL = Duration.ofMinutes(2);
}
