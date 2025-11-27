package com.example.captcha;

import com.example.captcha.config.CaptchaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CaptchaProperties.class)
public class CaptchaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaptchaServiceApplication.class, args);
    }
}
