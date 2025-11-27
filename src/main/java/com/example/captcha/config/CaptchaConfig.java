package com.example.captcha.config;

import com.example.captcha.service.CaptchaImageGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CaptchaConfig {

    @Bean
    public CaptchaImageGenerator captchaImageGenerator(){
        return new CaptchaImageGenerator();
    }
}
